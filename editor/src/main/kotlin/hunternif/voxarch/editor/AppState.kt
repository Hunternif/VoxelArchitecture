package hunternif.voxarch.editor

import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.storage.ChunkedStorage3D

/**
 * Contains data that completely defines app state.
 * It should only be modified via AppActions.
 */
interface AppState {
    //=============================== VOXELS ================================

    val voxels: SceneVoxelGroup


    //=========================== SCENE OBJECTS =============================

    /** Root node containing all nodes in the editor */
    val rootNode: SceneNode
    /** The node under which new child nodes would be added */
    val parentNode: SceneNode
    /** All objects in the scene, including nodes, voxels etc. */
    val sceneObjects: Collection<SceneObject>
    /** Objects currently selected by cursor, for modification or inspection.
     * Should not contain [rootNode]. */
    val selectedObjects: Set<SceneObject>
    /** Objects marked as hidden in UI, and invisible in 3d viewport. */
    val hiddenObjects: Set<SceneObject>
    /** Render-related data for each node. */
    val nodeObjectMap: Map<Node, SceneNode>


    //=============================== TOOLS =================================

    val currentTool: Tool
    val newNodeFrame: NewNodeFrame


    //============================= GUI STATE ===============================

    val DEBUG: Boolean
    val isMainWindowFocused: Boolean


    //============================ GUI SETTINGS =============================

    val gridMargin: Int
}

class AppStateImpl : AppState {
    override var voxels = SceneVoxelGroup(ChunkedStorage3D())

    override val rootNode = SceneNode(Structure())
    override var parentNode: SceneNode = rootNode
    override val sceneObjects: LinkedHashSet<SceneObject> = LinkedHashSet()
    override val selectedObjects: LinkedHashSet<SceneObject> = LinkedHashSet()
    override val hiddenObjects: LinkedHashSet<SceneObject> = LinkedHashSet()
    override val nodeObjectMap: LinkedHashMap<Node, SceneNode> = LinkedHashMap()

    override var currentTool: Tool = Tool.ADD_NODE
    override val newNodeFrame = NewNodeFrame()

    override val DEBUG = true
    override var isMainWindowFocused = false

    override var gridMargin = 9
}