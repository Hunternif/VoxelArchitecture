package hunternif.voxarch.editor

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.editor.actions.History
import hunternif.voxarch.editor.actions.HistoryAction
import hunternif.voxarch.editor.actions.ReadOnlyHistory
import hunternif.voxarch.editor.builder.generatorsByName
import hunternif.voxarch.editor.builder.mapVoxelToSolidColor
import hunternif.voxarch.editor.builder.setDefaultBuilders
import hunternif.voxarch.editor.builder.setSolidColorMaterials
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.world.defaultEnvironment

/**
 * Contains data that completely defines app state.
 * It should only be modified via AppActions.
 */
interface AppState {
    //=============================== VOXELS ================================

    /** Root group containing all voxel groups in the scene.
     * The root itself should stay empty of voxels. */
    val voxelRoot: SceneVoxelGroup
    val builder: MainBuilder
    val buildContext: BuildContext
    val stylesheet: Stylesheet
    val seed: Long
    val generatorNames: List<String>
    val generatedNodes: Collection<SceneNode>
    val generatedVoxels: Collection<SceneVoxelGroup>
    val voxelColorMap: (IVoxel) -> ColorRGBa


    //=========================== SCENE OBJECTS =============================

    /** Root node containing all nodes in the scene. */
    val rootNode: SceneNode
    /** The node under which new child nodes would be added. */
    val parentNode: SceneNode
    /** All objects in the scene, including nodes, voxels etc. */
    val sceneObjects: Collection<SceneObject>
    /** Objects currently selected by cursor, for modification or inspection.
     * Should not contain [rootNode]. */
    val selectedObjects: Set<SceneObject>
    /** Objects hidden either manually or via its parent being hidden. */
    val hiddenObjects: Set<SceneObject>
    /** Objects marked as hidden in UI, and invisible in 3d viewport. */
    val manuallyHiddenObjects: Set<SceneObject>


    //=============================== TOOLS =================================

    val currentTool: Tool
    val newNodeFrame: NewNodeFrame
    val history: ReadOnlyHistory<HistoryAction>
    val highlightedFace: AABBFace?


    //============================= GUI STATE ===============================

    val DEBUG: Boolean
    val isMainWindowFocused: Boolean
    val isMainWindowHovered: Boolean
    val errors: List<Exception>


    //============================ GUI SETTINGS =============================

    val gridMargin: Int
}

class AppStateImpl : AppState {
    override val voxelRoot = SceneVoxelGroup("Voxel groups", emptyArray3D())
    override val builder = MainBuilder()
    override val buildContext = BuildContext(defaultEnvironment).apply {
        materials.setSolidColorMaterials()
        builders.setDefaultBuilders()
        builders.setCastleBuilders()
    }
    override val stylesheet = defaultStyle
    override var seed: Long = 0
    override val generatorNames = generatorsByName.keys.toList()
    override val generatedNodes: LinkedHashSet<SceneNode> = LinkedHashSet()
    override val generatedVoxels: LinkedHashSet<SceneVoxelGroup> = LinkedHashSet()
    override val voxelColorMap = ::mapVoxelToSolidColor

    override var rootNode = SceneNode(Structure())
    override var parentNode: SceneNode = rootNode
    override val sceneObjects: LinkedHashSet<SceneObject> = LinkedHashSet()
    override val selectedObjects: LinkedHashSet<SceneObject> = LinkedHashSet()
    override val hiddenObjects: LinkedHashSet<SceneObject> = LinkedHashSet()
    override val manuallyHiddenObjects: LinkedHashSet<SceneObject> = LinkedHashSet()

    override var currentTool: Tool = Tool.ADD_NODE
    override val newNodeFrame = NewNodeFrame()
    override val history = History<HistoryAction>()
    override var highlightedFace: AABBFace? = null

    override val DEBUG = true
    override var isMainWindowFocused = false
    override var isMainWindowHovered = false
    override val errors = LinkedList<Exception>()

    override var gridMargin = 9
}