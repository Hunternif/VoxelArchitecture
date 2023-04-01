package hunternif.voxarch.editor

import com.google.common.collect.ListMultimap
import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import hunternif.voxarch.editor.builder.*
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.models.box.BoxFace
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.scene.shaders.VoxelShadingMode
import hunternif.voxarch.editor.scenegraph.*
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.world.defaultEnvironment
import java.nio.file.Path

/**
 * Contains data that completely defines app state.
 * It should only be modified via AppActions.
 */
interface AppState {
    //============================ PROJECT FILE =============================
    val projectPath: Path?
    val lastSavedAction: HistoryAction?
    val stylesheetText: String
    val blueprints: Collection<Blueprint>
    /** Maps BP to nodes where it's used */
    val blueprintUsage: ListMultimap<Blueprint, SceneNode>

    //=============================== VOXELS ================================

    /** Root group containing all voxel groups in the scene.
     * The root itself should stay empty of voxels. */
    val voxelRoot: SceneObject
    val builder: MainBuilder
    val buildContext: BuildContext
    val stylesheet: Stylesheet
    val seed: Long
    val domBuilderNames: List<String>
    val generatedNodes: Subset<SceneNode>
    val generatedVoxels: Subset<SceneVoxelGroup>
    val voxelColorMap: (IVoxel) -> ColorRGBa
    val renderMode: VoxelRenderMode
    val shadingMode: VoxelShadingMode


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
    val selectedObjects: Subset<SceneObject>
    /** Objects hidden either manually or via its parent being hidden. */
    val hiddenObjects: Subset<SceneObject>
    /** Objects marked as hidden in UI, and invisible in 3d viewport. */
    val manuallyHiddenObjects: Subset<SceneObject>


    //=============================== TOOLS =================================

    val currentTool: Tool
    val newNodeFrame: NewNodeFrame
    val history: ReadOnlyHistory<HistoryAction>
    val highlightedFace: BoxFace?
    val newNodeType: String


    //============================== OPTIONS ================================

    val cleanDummies: Boolean


    //============================= GUI STATE ===============================

    val DEBUG: Boolean
    val isMainWindowFocused: Boolean
    val isMainWindowHovered: Boolean
    val isTextEditorActive: Boolean
    val selectedBlueprint: Blueprint?
    /** Maps text id to text value*/
    val overlayText: Map<String, String>
}

class AppStateImpl(
    val registry: SceneRegistry,
    // base objects with IDs:
    val sceneRoot: SceneObject,
    override val rootNode: SceneNode,
    override val voxelRoot: SceneVoxelGroup,
    // base subsets with IDs:
    override val generatedNodes: Subset<SceneNode>,
    override val generatedVoxels: Subset<SceneVoxelGroup>,
    override val selectedObjects: Subset<SceneObject>,
    override val hiddenObjects: Subset<SceneObject>,
    override val manuallyHiddenObjects: Subset<SceneObject>,
) : AppState {
    override var projectPath: Path? = null
    override var lastSavedAction: HistoryAction? = null
    override var stylesheetText: String = ""
    override val blueprints get() = registry.blueprintIDs.map.values
    override val blueprintUsage = registry.bpInNodes

    override val builder = MainBuilder()
    override val buildContext = BuildContext(defaultEnvironment).apply {
        materials.setSolidColorMaterials()
        builders.setDefaultBuilders()
        builders.setCastleBuilders()
    }
    override var stylesheet = Stylesheet()
    override var seed: Long = 0
    override val domBuilderNames = domBuilderFactoryByName.keys.toList()
    override val voxelColorMap = ::mapVoxelToSolidColor
    override var renderMode = VoxelRenderMode.COLORED
    override var shadingMode = VoxelShadingMode.MAGICA_VOXEL

    override var parentNode: SceneNode = rootNode
    override val sceneTree = SceneTree(sceneRoot).apply {
        root.addChild(rootNode)
        items.remove(rootNode) // root node should not be listed under "items"
        root.addChild(voxelRoot)
        items.remove(voxelRoot) // voxel root node should not be listed under "items"
        subsets.add(selectedObjects)
        subsets.add(hiddenObjects)
        subsets.add(manuallyHiddenObjects)
        subsets.add(generatedNodes)
        subsets.add(generatedVoxels)
    }

    override var currentTool: Tool = Tool.ADD_NODE
    override val newNodeFrame = NewNodeFrame()
    override val history = History<HistoryAction>()
    override var highlightedFace: BoxFace? = null
    override var newNodeType: String = "Node"

    override var cleanDummies: Boolean = true

    override val DEBUG = true
    override var isMainWindowFocused = false
    override var isMainWindowHovered = false
    override var isTextEditorActive = false
    override var selectedBlueprint: Blueprint? = null
    override val overlayText = linkedMapOf<String, String>()
}

/** Create a new clean state. */
fun newState(): AppStateImpl {
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