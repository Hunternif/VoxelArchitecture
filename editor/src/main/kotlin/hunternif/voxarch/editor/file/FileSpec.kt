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
  - nodes.xml
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

1. Nodes
Serialized to XML. Raw node objects are mapped to a DTO class and serialized
via annotations.

    Example:
    <structure origin="(0, 0, 0)">
        <room>
            <wall>
            <wall>
        </room>
    </structure>


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

            // The subsets are default so far:
            val generatedNodes = reg.newSubset<SceneNode>("generated nodes")
            val generatedVoxels = reg.newSubset<SceneVoxelGroup>("generated voxels")
            val selectedObjects = reg.newSubset<SceneObject>("selected")
            val hiddenObjects = reg.newSubset<SceneObject>("hidden")
            val manuallyHiddenObjects = reg.newSubset<SceneObject>("manually hidden")
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
            )
            val treeXmlStr = serializeToXmlStr(treeXmlType, true)
            it.write(treeXmlStr)
        }
    }
}
