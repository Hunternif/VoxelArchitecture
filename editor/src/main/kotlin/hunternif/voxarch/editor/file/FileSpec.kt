package hunternif.voxarch.editor.file

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.editor.AppStateImpl
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.logError
import hunternif.voxarch.editor.actions.logWarning
import hunternif.voxarch.editor.actions.scene.BuildVoxels.Companion.BUILT_VOXELS_GROUP_NAME
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.builder.BuilderLibrary
import hunternif.voxarch.editor.builder.SolidColorBlock
import hunternif.voxarch.editor.file.style.StyleParser
import hunternif.voxarch.editor.scenegraph.*
import hunternif.voxarch.editor.util.newZipFileSystem
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.storage.BlockStorageDelegate
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.util.copyTo
import hunternif.voxarch.util.forEachSubtree
import java.io.BufferedWriter
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.util.stream.Collectors
import kotlin.io.path.isRegularFile

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
fun EditorApp.readProject(path: Path): AppStateImpl {
    val reg = SceneRegistry()
    val bpReg = BlueprintRegistry()
    val builderLibrary = BuilderLibrary()
    val sceneRoot = reg.newObject()
    // Blueprints in format < 8 used IDs:
    val legacyBpIdMap = mutableMapOf<Int, Blueprint>()

    val zipfs = newZipFileSystem(path)
    zipfs.use {
        val metadata = readMetadata(path, zipfs, this)
        val treeXmlType = readSceneTree(zipfs)

        readBlueprints(zipfs, bpReg, legacyBpIdMap, this)
        tryPopulateDelegateBlueprints(bpReg, legacyBpIdMap, metadata)

        // populate VOX files, Blueprints, custom Builders
        treeXmlType.noderoot?.forEachSubtree {
            tryReadVoxFile(it, zipfs, metadata, this)
            tryAddBlueprintRefs(it, bpReg, legacyBpIdMap, metadata)
            tryAddBuilderRef(it, builderLibrary)
        }
        treeXmlType.voxelroot?.forEachSubtree { tryReadVoxFile(it, zipfs, metadata, this) }

        val rootNode = treeXmlType.noderoot?.mapXml() as SceneNode
        reg.save(rootNode)

        val voxelRoot = treeXmlType.voxelroot?.mapXml() as SceneVoxelGroup
        reg.save(voxelRoot)

        val generatedNodes = treeXmlType.generatedNodes!!.mapXmlSubset<SceneNode>(reg)
        val generatedVoxels = treeXmlType.generatedVoxels!!.mapXmlSubset<SceneVoxelGroup>(reg)
        val selectedObjects = treeXmlType.selectedObjects!!.mapXmlSubset<SceneObject>(reg)
        val hiddenObjects = treeXmlType.hiddenObjects!!.mapXmlSubset<SceneObject>(reg)
        val manuallyHiddenObjects = treeXmlType.manuallyHiddenObjects!!.mapXmlSubset<SceneObject>(reg)

        // Stylesheet
        val styleText = tryReadStylesheetFile(zipfs)
        // Ignore parsing errors, they will show in the editor anyway
        val style = Stylesheet.fromRules(StyleParser().parseStylesheet(styleText).rules)

        return AppStateImpl(
            reg,
            bpReg,
            builderLibrary,
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
            stylesheet = style
            stylesheetText = styleText
            seed = metadata.seed
            blueprintRegistry.refreshUsages(this)
        }
    }
}

/**
 * Writes app state into a project file.
 * Not all state will be written, see the file spec above.
 */
fun EditorAppImpl.writeProject(path: Path) {
    val zipfs = newZipFileSystem(path)
    val paths = mutableListOf<Path>()

    fun writeFile(path: String, block: (BufferedWriter) -> Unit) {
        val pathObj = zipfs.getPath(path)
        paths.add(pathObj)
        Files.newBufferedWriter(pathObj, CREATE, TRUNCATE_EXISTING).use(block)
    }

    zipfs.use {
        writeFile("/metadata.yaml") {
            val metadata = Metadata(FORMAT_VERSION, state.seed)
            val metadataStr = serializeToYamlStr(metadata)
            it.write(metadataStr)
        }

        writeFile("/scenetree.xml") {
            val treeXmlType = XmlSceneTree(
                noderoot = state.rootNode.mapToXml(),
                voxelroot = state.voxelRoot.mapToXml(),
                generatedNodes = state.generatedNodes.mapToXml(),
                generatedVoxels = state.generatedVoxels.mapToXml(),
                selectedObjects = state.selectedObjects.mapToXml(),
                hiddenObjects = state.hiddenObjects.mapToXml(),
                manuallyHiddenObjects = state.manuallyHiddenObjects.mapToXml(),
            )
            val treeXmlStr = serializeToXmlStr(treeXmlType, true)
            it.write(treeXmlStr)
        }

        Files.createDirectories(zipfs.getPath("/voxels"))
        fun tryWriteVoxFile(obj: SceneObject) {
            if (obj is SceneVoxelGroup && obj.data.isNotEmpty()) {
                val voxPath = zipfs.getPath("/voxels/group_${obj.id}.vox")
                paths.add(voxPath)
                obj.data.writeToVoxFile(voxPath) { v ->
                    if (v is VoxColor) v
                    else VoxColor(state.voxelColorMap(v).hex)
                }
            }
        }
        state.rootNode.forEachSubtree { tryWriteVoxFile(it) }
        state.voxelRoot.forEachSubtree { tryWriteVoxFile(it) }

        Files.createDirectories(zipfs.getPath("/blueprints"))
        state.blueprintRegistry.blueprints.forEachIndexed { i, bp ->
            writeFile("/blueprints/blueprint_$i.xml") {
                val bpJson = serializeToXmlStr(bp, true)
                it.write(bpJson)
            }
        }

        writeFile("/stylesheet.vcss") {
            it.write(state.stylesheetText)
        }

        // Delete old unused files:
        Files.walk(zipfs.getPath("/"), 2)
            .filter { it !in paths }
            .filter { it.isRegularFile() }
            .forEach {
                Files.delete(it)
            }
    }
}

internal fun readMetadata(path: Path, zipfs: FileSystem, app: EditorApp): Metadata {
    val metadata = Files.newBufferedReader(zipfs.getPath("/metadata.yaml")).use {
        deserializeYaml(it.readText(), Metadata::class)
    }
    if (metadata.formatVersion < FORMAT_VERSION) {
        app.logWarning(
            "Attempting to open $path which uses old version ${metadata.formatVersion}, " +
                "while current version is $FORMAT_VERSION. " +
                "Things may not work as expected."
        )
    } else if (metadata.formatVersion > FORMAT_VERSION) {
        app.logWarning(
            "Attempting to open $path which uses NEW version ${metadata.formatVersion}, " +
                "while current version is $FORMAT_VERSION. " +
                "Things will probably not work as expected."
        )
    }
    return metadata
}

private fun readSceneTree(zipfs: FileSystem): XmlSceneTree =
    Files.newBufferedReader(zipfs.getPath("/scenetree.xml")).use {
        deserializeXml(it.readText(), XmlSceneTree::class)
    }

private fun tryReadVoxFile(
    obj: XmlSceneObject, fs: FileSystem, metadata: Metadata, app: EditorApp,
) {
    val voxPath = fs.getPath("/voxels/group_${obj.id}.vox")
    if (obj is XmlSceneVoxelGroup) {
        try {
            val useModelOffset = metadata.formatVersion >= 3
            val vox = readVoxFile(voxPath, useModelOffset)
            if (obj.label == BUILT_VOXELS_GROUP_NAME) {
                // Map to IBlockStorage:
                val newData = BlockStorageDelegate(ChunkedStorage3D())
                vox.copyTo(newData) { voxColor -> SolidColorBlock(voxColor.color) }
                obj.data = newData
                //TODO: create a universal voxel storage format that retains
                // all Editor data.
            } else {
                obj.data = vox
            }
        } catch (e: java.nio.file.NoSuchFileException) {
            // ignore this because data could have been empty
        } catch (e: Exception) {
            app.logWarning("Couldn't read voxel group ${obj.id}")
            app.logError(e)
        }
    }
}

/** Finds and adds Blueprint references by ID, if they have been loaded. */
private fun tryAddBlueprintRefs(
    obj: XmlSceneObject,
    bpReg: BlueprintRegistry,
    /** For legacy BPs in format < 8*/
    idMap: MutableMap<Int, Blueprint>,
    metadata: Metadata,
) {
    (obj as? XmlSceneNode)?.let { node ->
        node.blueprintRefs =
            if (metadata.formatVersion >= 8) {
                node.blueprintNames.mapNotNull {
                    bpReg.blueprintsByName[it]
                }
            } else {
                node.blueprintIDs.mapNotNull {
                    idMap[it]
                }
            }
    }
}

/** Finds and adds Builder references by name, if they have been loaded. */
private fun tryAddBuilderRef(obj: XmlSceneObject, lib: BuilderLibrary) {
    (obj as? XmlSceneNode)?.let { node ->
        val builderName = node.node?.builder ?: return
        node.builderRef = lib.buildersByName[builderName]?.builder
    }
}

internal fun readBlueprints(
    fs: FileSystem,
    bpReg: BlueprintRegistry,
    /** For legacy BPs in format < 8*/
    idMap: MutableMap<Int, Blueprint>,
    app: EditorApp,
) {
    val files = Files.list(fs.getPath("/blueprints")).collect(Collectors.toList())
    for (file in files) {
        try {
            Files.newBufferedReader(file).use {
                val xmlBp = deserializeXml(it.readText(), XmlBlueprint::class)
                val bp = xmlBp.mapXml()
                bpReg.save(bp)
                xmlBp.id?.let { idMap[it] = bp }
            }
        } catch (e: Exception) {
            app.logWarning("Couldn't read blueprint $file")
            app.logError(e)
        }
    }
}

private fun tryReadStylesheetFile(fs: FileSystem): String {
    try {
        Files.newBufferedReader(fs.getPath("/stylesheet.vcss")).use {
            return it.readText()
        }
    } catch (e: java.nio.file.NoSuchFileException) {
        return defaultStyle.toString()
    }
}

/**
 * Populate blueprint nodes that reference other blueprints.
 */
fun tryPopulateDelegateBlueprints(
    bpReg: BlueprintRegistry,
    /** For legacy BPs in format < 8*/
    idMap: MutableMap<Int, Blueprint>,
    metadata: Metadata,
) {
    val mapByName = bpReg.blueprintsByName
    bpReg.blueprints.forEach { bp ->
        for (node in bp.nodes) {
            val domBuilder = node.domBuilder as? DomRunBlueprint ?: continue
            val delegateBp = when {
                metadata.formatVersion >= 7 -> mapByName[domBuilder.blueprintName]
                else -> idMap[domBuilder.blueprintID]
            } ?: continue
            domBuilder.blueprint = delegateBp
            // Refresh out slots:
            delegateBp.outNodes.forEach { outNode ->
                // TODO: this logic is complex and duplicated across BP actions:
                //  new node, delete node, set delegate. Refactor!
                val slotSource = outNode.domBuilder as DomBlueprintOutSlot
                val slotInstance = DomBlueprintOutSlotInstance(slotSource)
                val existingSlot = node.outputs.firstOrNull { it.name == slotSource.slotName }
                val slot = if (existingSlot != null) {
                    existingSlot.domSlot = slotInstance
                    existingSlot.links.forEach { it.relink() }
                    existingSlot
                } else {
                    node.addOutput(slotSource.slotName, slotInstance)
                }
                domBuilder.outSlots.add(slot)
            }
        }
    }
}
