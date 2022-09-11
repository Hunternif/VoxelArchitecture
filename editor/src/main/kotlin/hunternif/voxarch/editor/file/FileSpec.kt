package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.AppState
import hunternif.voxarch.editor.AppStateImpl
import hunternif.voxarch.editor.scenegraph.*
import hunternif.voxarch.editor.util.newZipFileSystem
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
    - generated.vox
    - imported-1.vox
    ...
  / generators
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


3. Generators
Using text format that matches Kotlin "Castle DSL" code.
- also could be XML, because tree structure.
Serializers will be code-generated from annotated DSL builder methods.
    Example: see CastleWardTest.kt

*/

const val VOXARCH_PROJECT_FILE_EXT = "voxarch"

/**
 * Reads the project from file and produces a new app state.
 */
fun readProject(path: Path): AppStateImpl {
    val reg = SceneRegistry()
    val sceneRoot = reg.newObject()

    val zipfs = newZipFileSystem(path)
    zipfs.use {
        Files.newBufferedReader(zipfs.getPath("/scenetree.xml")).use {
            val treeXmlType = deserializeXml(it.readText(), XmlSceneTree::class)

            val rootNode = treeXmlType.noderoot?.mapXml() as SceneNode
            reg.save(rootNode)

            val voxelRoot = treeXmlType.voxelroot?.mapXml() as SceneVoxelGroup
            reg.save(voxelRoot)

            val generatedNodes = treeXmlType.generatedNodes!!.mapXmlSubset<SceneNode>(reg)
            val generatedVoxels = treeXmlType.generatedVoxels!!.mapXmlSubset<SceneVoxelGroup>(reg)
            val selectedObjects = treeXmlType.selectedObjects!!.mapXmlSubset<SceneObject>(reg)
            val hiddenObjects = treeXmlType.hiddenObjects!!.mapXmlSubset<SceneObject>(reg)
            val manuallyHiddenObjects = treeXmlType.manuallyHiddenObjects!!.mapXmlSubset<SceneObject>(reg)

            return AppStateImpl(
                reg,
                sceneRoot,
                rootNode,
                voxelRoot,
                generatedNodes,
                generatedVoxels,
                selectedObjects,
                hiddenObjects,
                manuallyHiddenObjects
            )
        }
    }
}

/**
 * Writes app state into a project file.
 * Not all state will be written, see the file spec above.
 */
fun writeProject(state: AppState, path: Path) {
    val zipfs = newZipFileSystem(path)
    zipfs.use {
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
            )
            val treeXmlStr = serializeToXmlStr(treeXmlType, true)
            it.write(treeXmlStr)
        }
    }
}
