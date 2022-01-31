package hunternif.voxarch.editor

import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.models.NodeModel.NodeData
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.storage.IStorage3D

/**
 * Contains data that completely defines app state.
 * It should only be modified via AppActions.
 */
interface AppState {
    //=============================== VOXELS ================================

    val voxels: IStorage3D<VoxColor?>


    //=============================== NODES =================================

    /** Root node containing everything in the editor */
    val rootNode: Node
    /** The node under which new child nodes would be added */
    val parentNode: Node
    /** Nodes currently selected by cursor, for batch modification or inspection.
     * Should not contain [rootNode]. */
    val selectedNodes: Set<Node>

    /** Nodes marked as hidden in UI, and invisible in 3d viewport. */
    val hiddenNodes: Set<Node>

    /** Render-related data for each node. */
    val nodeDataMap: Map<Node, NodeData>


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
    override var voxels: IStorage3D<VoxColor?> = ChunkedStorage3D()

    override val rootNode: Structure = Structure()
    override var parentNode: Node = rootNode
    override val selectedNodes: LinkedHashSet<Node> = LinkedHashSet()
    override val hiddenNodes: MutableSet<Node> = mutableSetOf()
    override val nodeDataMap: LinkedHashMap<Node, NodeData> = LinkedHashMap()

    override var currentTool: Tool = Tool.ADD_NODE
    override val newNodeFrame = NewNodeFrame()

    override val DEBUG = true
    override var isMainWindowFocused = false

    override var gridMargin = 9
}