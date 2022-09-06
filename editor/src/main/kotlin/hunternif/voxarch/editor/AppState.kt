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
import hunternif.voxarch.editor.scenegraph.*
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.world.defaultEnvironment
import java.nio.file.Path
import java.util.LinkedList

/**
 * Contains data that completely defines app state.
 * It should only be modified via AppActions.
 */
interface AppState {
    //============================ PROJECT FILE =============================
    val projectPath: Path?

    //=============================== VOXELS ================================

    /** Root group containing all voxel groups in the scene.
     * The root itself should stay empty of voxels. */
    val voxelRoot: SceneObject
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
    /** Contains all objects in the scene. */
    val sceneTree: SceneTree
    /** All objects in the scene, including nodes, voxels etc. */
    val sceneObjects: Collection<SceneObject> get() = sceneTree.items
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

class AppStateImpl(
    val registry: SceneRegistry,
    // base objects with IDs:
    val sceneRoot: SceneObject,
    override val rootNode: SceneNode,
    override val voxelRoot: SceneVoxelGroup,
    // base subsets with IDs:
    override val generatedNodes: SceneTree.Subset<SceneNode>,
    override val generatedVoxels: SceneTree.Subset<SceneVoxelGroup>,
    override val selectedObjects: SceneTree.Subset<SceneObject>,
    override val hiddenObjects: SceneTree.Subset<SceneObject>,
    override val manuallyHiddenObjects: SceneTree.Subset<SceneObject>,
) : AppState {
    override var projectPath: Path? = null

    override val builder = MainBuilder()
    override val buildContext = BuildContext(defaultEnvironment).apply {
        materials.setSolidColorMaterials()
        builders.setDefaultBuilders()
        builders.setCastleBuilders()
    }
    override val stylesheet = defaultStyle
    override var seed: Long = 0
    override val generatorNames = generatorsByName.keys.toList()
    override val voxelColorMap = ::mapVoxelToSolidColor

    override var parentNode: SceneNode = rootNode
    override val sceneTree = SceneTree(sceneRoot).apply {
        root.attach(rootNode)
        items.remove(rootNode) // root node should not be listed under "items"
        root.attach(voxelRoot)
        items.remove(voxelRoot) // voxel root node should not be listed under "items"
        subsets.add(selectedObjects)
        subsets.add(hiddenObjects)
        subsets.add(manuallyHiddenObjects)
    }

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

/** Create a new clean state. */
fun newState() : AppStateImpl {
    val reg = SceneRegistry()
    return AppStateImpl(
        registry = reg,
        sceneRoot = reg.newObject(),
        rootNode = reg.newNode(Structure()),
        voxelRoot = reg.newVoxelGroup("Voxel groups", emptyArray3D()),
        generatedNodes = reg.newSubset<SceneNode>("generated nodes"),
        generatedVoxels = reg.newSubset<SceneVoxelGroup>("generated voxels"),
        selectedObjects = reg.newSubset("selected"),
        hiddenObjects = reg.newSubset("hidden"),
        manuallyHiddenObjects = reg.newSubset("manually hidden"),
    )
}