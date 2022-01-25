package hunternif.voxarch.editor

import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.models.NodeModel.NodeData
import hunternif.voxarch.editor.util.VoxelAABBf
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure

/**
 * Contains data that completely defines app state.
 * It should only be modified via AppActions.
 */
interface AppState {
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

    /** Area where you are allowed to place new voxels. The grid matches it. */
    val workArea: VoxelAABBf

    val newNodeFrame: NewNodeFrame


    //============================= GUI STATE ===============================

    val DEBUG: Boolean
    val isMainWindowFocused: Boolean


    //============================ GUI SETTINGS =============================

    val gridMargin: Int
}

class AppStateImpl : AppState {
    override val rootNode: Structure = Structure()
    override var parentNode: Node = rootNode
    override val selectedNodes: LinkedHashSet<Node> = LinkedHashSet()
    override val hiddenNodes: MutableSet<Node> = mutableSetOf()
    override val nodeDataMap: LinkedHashMap<Node, NodeData> = LinkedHashMap()

    override var currentTool: Tool = Tool.ADD_NODE
    override val workArea = VoxelAABBf()
    override val newNodeFrame = NewNodeFrame()

    override val DEBUG = true
    override var isMainWindowFocused = false

    override var gridMargin = 9
}