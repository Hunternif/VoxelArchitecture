package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.AppStateImpl
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.logError
import hunternif.voxarch.editor.actions.logWarning
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.scenegraph.*
import hunternif.voxarch.editor.util.newZipFileSystem
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.util.forEachSubtree
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING

/*

Project folder structure:

/ project.voxarch
  - metadata.yaml
  - scenetree.xml
  / voxels
    - group_0.vox
    - group_1.vox
    ...
  / blueprints
    - turret.kt
    - castle.kt
    ...

All contents are stored as separate files in a folder.
The folder could be zipped into a single file.


0. Metadata
Format version, project name etc.

1. Scene Tree
Serialized to XML. Raw node objects are mapped to a DTO class and serialized
via annotations.
Nodes are contained within SceneObjects.
Subsets are serialized as a list of ids.

    Example:
    <obj id="0">
        <structure origin="(0, 0, 0)"/>
        <obj id="1">
            <room/>
            <obj id="2">
                <wall/>
            </obj>
            <obj id="3">
                <wall/>
            </obj>
        </obj>
    </obj>
    <subset name="hidden">
        <item>1</item>
    </subset>


2. Voxels
Serialized to VOX.
NOTE: generated voxels can have negative coordinates, which isn't supported by
MagicaVoxel. The reading and writing works for this project, but viewing in MV
can cause issues.


3. Blueprints
(below was designed for DomBuilders, could be adapted to Blueprints)
Using text format that matches Kotlin "Castle DSL" code.
- also could be XML, because tree structure.
Serializers will be code-generated from annotated DSL builder methods.
    Example: see CastleWardTest.kt

*/

const val VOXARCH_PROJECT_FILE_EXT = "voxarch"

/**
 * Reads the project from file and produces a new app state.
 */
fun EditorAppImpl.readProject(path: Path) {
    val reg = SceneRegistry()
    val sceneRoot = reg.newObject()

    val zipfs = newZipFileSystem(path)
    zipfs.use {
        val metadata = Files.newBufferedReader(zipfs.getPath("/metadata.yaml")).use {
            deserializeYaml(it.readText(), Metadata::class)
        }
        if (metadata.formatVersion < FORMAT_VERSION) {
            logWarning(
                "Attempting to open $path which uses old version ${metadata.formatVersion}, " +
                    "while current version is $FORMAT_VERSION. " +
                    "Things may not work as expected."
            )
        }

        val treeXmlType = Files.newBufferedReader(zipfs.getPath("/scenetree.xml")).use {
            deserializeXml(it.readText(), XmlSceneTree::class)
        }

        treeXmlType.blueprints?.entries?.forEach {
            tryReadBlueprintFile(it, zipfs, reg, this)
        }

        // populate VOX files & Blueprints
        treeXmlType.noderoot?.forEachSubtree {
            zipfs.tryReadVoxFile(it, metadata)
            tryAddBlueprintRefs(it, reg)
        }
        treeXmlType.voxelroot?.forEachSubtree { zipfs.tryReadVoxFile(it, metadata) }

        val rootNode = treeXmlType.noderoot?.mapXml() as SceneNode
        reg.save(rootNode)

        val voxelRoot = treeXmlType.voxelroot?.mapXml() as SceneVoxelGroup
        reg.save(voxelRoot)

        val generatedNodes = treeXmlType.generatedNodes!!.mapXmlSubset<SceneNode>(reg)
        val generatedVoxels = treeXmlType.generatedVoxels!!.mapXmlSubset<SceneVoxelGroup>(reg)
        val selectedObjects = treeXmlType.selectedObjects!!.mapXmlSubset<SceneObject>(reg)
        val hiddenObjects = treeXmlType.hiddenObjects!!.mapXmlSubset<SceneObject>(reg)
        val manuallyHiddenObjects = treeXmlType.manuallyHiddenObjects!!.mapXmlSubset<SceneObject>(reg)

        state = AppStateImpl(
            reg,
            sceneRoot,
            rootNode,
            voxelRoot,
            generatedNodes,
            generatedVoxels,
            selectedObjects,
            hiddenObjects,
            manuallyHiddenObjects
        ).apply {
            projectPath = path
        }
    }
}

/**
 * Writes app state into a project file.
 * Not all state will be written, see the file spec above.
 */
fun EditorAppImpl.writeProject(path: Path) {
    val zipfs = newZipFileSystem(path)
    zipfs.use {
        Files.newBufferedWriter(
            zipfs.getPath("/metadata.yaml"),
            CREATE,
            TRUNCATE_EXISTING
        ).use {
            val metadata = Metadata(FORMAT_VERSION)
            val metadataStr = serializeToYamlStr(metadata)
            it.write(metadataStr)
        }

        Files.newBufferedWriter(
            zipfs.getPath("/scenetree.xml"),
            CREATE,
            TRUNCATE_EXISTING
        ).use {
            val treeXmlType = XmlSceneTree(
                noderoot = state.rootNode.mapToXml(),
                voxelroot = state.voxelRoot.mapToXml(),
                generatedNodes = state.generatedNodes.mapToXml(),
                generatedVoxels = state.generatedVoxels.mapToXml(),
                selectedObjects = state.selectedObjects.mapToXml(),
                hiddenObjects = state.hiddenObjects.mapToXml(),
                manuallyHiddenObjects = state.manuallyHiddenObjects.mapToXml(),
                blueprints = state.registry.blueprintIDs.mapToXml(),
            )
            val treeXmlStr = serializeToXmlStr(treeXmlType, true)
            it.write(treeXmlStr)
        }

        Files.createDirectories(zipfs.getPath("/voxels"))
        fun tryWriteVoxFile(obj: SceneObject) {
            if (obj is SceneVoxelGroup && obj.data.isNotEmpty()) {
                val voxPath = zipfs.getPath("/voxels/group_${obj.id}.vox")
                obj.data.writeToVoxFile(voxPath) { v ->
                    if (v is VoxColor) v
                    else VoxColor(state.voxelColorMap(v).hex)
                }
            }
        }
        state.rootNode.forEachSubtree { tryWriteVoxFile(it) }
        state.voxelRoot.forEachSubtree { tryWriteVoxFile(it) }

        Files.createDirectories(zipfs.getPath("/blueprints"))
        state.registry.blueprintIDs.map.forEach { (id, bp) ->
            Files.newBufferedWriter(
                zipfs.getPath("/blueprints/blueprint_$id.json"),
                CREATE,
                TRUNCATE_EXISTING
            ).use {
                val bpJson = serializeToJsonStr(bp, true)
                it.write(bpJson)
            }
        }
    }
}

private fun FileSystem.tryReadVoxFile(obj: XmlSceneObject, metadata: Metadata) {
    val voxPath = getPath("/voxels/group_${obj.id}.vox")
    if (obj is XmlSceneVoxelGroup) {
        try {
            val useModelOffset = metadata.formatVersion >= 3
            obj.data = readVoxFile(voxPath, useModelOffset)
        } catch (_: Exception) {
            // data could have been empty
        }
    }
}

/** Finds and adds Blueprint references by ID, if they have been loaded. */
private fun tryAddBlueprintRefs(obj: XmlSceneObject, reg: SceneRegistry) {
    (obj as? XmlSceneNode)?.let { node ->
        node.blueprintRefs = node.blueprintIDs.mapNotNull {
            reg.blueprintIDs.map[it]
        }
    }
}

private fun tryReadBlueprintFile(
    entry: XmlBlueprintEntry, fs: FileSystem, reg: SceneRegistry, app: EditorApp,
) {
    try {
        Files.newBufferedReader(
            fs.getPath("/blueprints/blueprint_${entry.id}.json")
        ).use {
            val bp = deserializeJson(it.readText(), Blueprint::class)
            reg.blueprintIDs.save(bp)
        }
    } catch (e: Exception) {
        app.logWarning("Couldn't read blueprint ${entry.id}")
        app.logError(e)
    }
}