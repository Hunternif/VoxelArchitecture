package hunternif.voxarch.editor.gui

import hunternif.voxarch.builder.Builder
import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.builder.BuilderLibrary
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.plan.ClipMask
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.naturalSize
import hunternif.voxarch.util.SnapOrigin
import hunternif.voxarch.vector.Vec3
import imgui.ImGui
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags

/**
 * Displays properties of a node, and updates them at an interval.
 * TODO: unit test GuiNodeProperties
 */
class GuiObjectProperties(
    private val app: EditorApp,
) {
    private val originInput = GuiInputVec3("origin")
    private val sizeInput = GuiInputVec3("voxel size", min = 1f)
    private val startInput = GuiInputVec3("start")
    private val tagsInput = GuiInputText("tags")
    private val snapOriginInput = GuiCombo("snap origin", *SnapOrigin.values())
    private val rotationInput = GuiInputFloat("rotation", speed = 5f, min = -360f, max = 360f)
    private val clipInput = GuiCombo("clip mask", *ClipMask.values())
    private val colorInput = GuiInputColor("color")
    private val builderInput by lazy {
        GuiCombo("builder", allBuilders)
    }
    private val blueprintInput by lazy {
        GuiCombo("##blueprint", allBlueprints)
    }

    // Update timer
    private val nodeTimer = Timer(0.02)
    private val builderTimer = Timer(0.1)
    private val blueprintsTimer = Timer(0.1)
    private val redrawTimer = Timer(0.02)

    /** Currently selected item */
    var obj: SceneObject? = null
        set(value) {
            field = value
            updateInputIDs()
        }
    private val voxGroupSize: Vec3 = Vec3(0, 0, 0)

    /** Default entry that indicates which builder will be assigned by BuilderConfig */
    private var defaultBuilderEntry = BuilderLibrary.Entry("Default", nullBuilder)
    /** Selected builder in combo box */
    private var builderEntry: BuilderLibrary.Entry = defaultBuilderEntry
    private val allBuilders = mutableListOf(defaultBuilderEntry)

    /** Default blueprint entry that causes a new blueprint to be created */
    private val newBlueprintItem = Blueprint("New...")
    /** Selected blueprint in combo box */
    private var selectedBlueprint = newBlueprintItem
    /** All blueprints currently on this node */
    private val curBlueprints = mutableListOf<Blueprint>()
    private val allBlueprints = mutableListOf(newBlueprintItem)

    /** Node tags in a single string, separated by whitespace */
    private var tagStr = ""
    /** Node tags memoized */
    private val tags = linkedSetOf<String>()

    fun render() {
        updateNodeData()
        renderHeaderText()

        obj?.let { obj ->
            when (obj) {
                is SceneNode -> renderNode(obj)
                is SceneVoxelGroup -> renderVoxelGroup(obj)
            }
        }
        redrawIfNeeded()
    }

    private fun renderNode(sceneNode: SceneNode) {
        val node = sceneNode.node
        // By passing the original Vec3 ref into render(), its value will be
        // updated in real time.
        originInput.render(node.origin) {
            app.transformObjOrigin(sceneNode, original, newValue)
        }
        tooltip("""The point from which this node's coordinates are calculated.
Indicated on the 3D scene as a little circle.""")

        sizeInput.render(node.naturalSize) {
            app.transformNodeNaturalSize(sceneNode, original, newValue)
        }
        tooltip("How many blocks this node occupies along each axis.")

        startInput.render(node.start) {
            app.transformNodeStart(sceneNode, original, newValue)
        }
        tooltip("""Internal offset of the low-XYZ corner.
Children are placed relative to parent's origin, but parent's start
suggests where children should be placed.
By default, it's set so that origin is at the low-XYZ corner.""")

        tagsInput.render(tagStr) {
            app.setNodeTags(sceneNode, it.split(Regex("\\s+")))
            updateTagStr()
        }
        tooltip("Node tags, separated by whitespace")

        snapOriginInput.render(sceneNode.snapOrigin) {
            app.transformNodeSnapOrigin(sceneNode, it)
        }
        tooltip("Snap node origin to a predefined position")

        rotationInput.render(node.rotationY.toFloat(), onUpdated = {
            node.rotationY = newValue.toDouble()
        }, onEditFinished = {
            app.transformNodeRotation(sceneNode, original.toDouble(), newValue.toDouble())
        })
        tooltip("CCW rotation around the vertical axis, in degrees.")

        checkNodeBuilder(sceneNode.node)
        builderInput.render(builderEntry) {
            //TODO: allow executing various builders regardless of exact node type
            builderEntry = it
            val newBuilder = if (it == defaultBuilderEntry) null else it.builder
            app.setNodeBuilder(sceneNode, newBuilder)
        }
        tooltip("Override the Builder that will be used for this node during 'Build voxels'.")

        clipInput.render(node.clipMask) {
            app.transformNodeClipMask(sceneNode, it)
        }
        tooltip("Node's children will only place voxels inside this mask.")

        colorInput.render(sceneNode.color) {
            app.transformNodeColor(sceneNode, original, newValue)
        }

        ImGui.separator()
        ImGui.text("Blueprints")
        blueprintInput.render(selectedBlueprint) { selectedBlueprint = it }
        ImGui.sameLine()
        button("Add") {
            if (selectedBlueprint == newBlueprintItem) app.addNewBlueprint(sceneNode)
            else app.addBlueprint(sceneNode, selectedBlueprint)
            selectedBlueprint = newBlueprintItem
        }
        renderBlueprintTable(sceneNode)
    }

    private fun renderBlueprintTable(sceneNode: SceneNode) {
        updateBlueprints(sceneNode)
        ImGui.pushFont(GuiBase.fontSmallIcons)
        if (ImGui.beginTable("blueprints_table", 2, ImGuiTableFlags.PadOuterX)) {
            // it's not actually 10px wide, selectable makes it wider
            ImGui.tableSetupColumn("icon", ImGuiTableColumnFlags.WidthFixed, 10f)
            ImGui.tableSetupColumn("name")

            curBlueprints.forEachIndexed { i, bp ->
                ImGui.tableNextRow()
                ImGui.tableNextColumn()
                centeredText(FontAwesomeIcons.Code)

                ImGui.tableNextColumn()
                val name = memoStrWithIndex(bp.name, i)
                val isSelected = app.state.selectedBlueprint == bp
                selectable(name, isSelected, true) {
                    app.selectBlueprint(bp)
                }
                contextMenu(memoStrWithIndex("bp_obj_context_menu", i)) {
                    menuItem("Remove from node") {
                        app.removeBlueprint(sceneNode, bp)
                    }
                }
            }
            ImGui.endTable()
        }
        ImGui.popFont()
    }

    private fun updateBlueprints(sceneNode: SceneNode) = blueprintsTimer.runAtInterval {
        // Check if new blueprints were added to the node
        curBlueprints.clear()
        curBlueprints.addAll(sceneNode.blueprints)
        // Check if new blueprints were added to the library
        if (allBlueprints.size != app.state.blueprints.size + 1) {
            // The combo input uses this list directly.
            // If we change its content, the input will update too.
            allBlueprints.apply {
                clear()
                add(newBlueprintItem)
                addAll(app.state.blueprints)
            }
        }
        // Check if any blueprints were renamed
        blueprintInput.refreshNames()
    }

    private fun renderVoxelGroup(group: SceneVoxelGroup) {
        originInput.render(group.origin) {
            app.transformObjOrigin(group, original, newValue)
        }
        disabled {
            voxGroupSize.set(group.data.sizeVec)
            sizeInput.render(voxGroupSize)
        }
    }

    /** Do any expensive updates to gui state based on changes in node data */
    private fun updateNodeData() = nodeTimer.runAtInterval {
        updateTagStr()
    }

    /** Check whether the list of current & available builders needs to be updated */
    private fun checkNodeBuilder(node: Node) = builderTimer.runAtInterval {
        val library = app.state.builderLibrary
        val builderConfig = app.state.buildContext.builders

        // Update the title of the default builder
        val newDefaultBuilder = builderConfig.getFromConfig(node) ?: nullBuilder
        if (defaultBuilderEntry.builder != newDefaultBuilder) {
            val newDefaultName = library.buildersByInstance[newDefaultBuilder]?.let {
                "Default: ${it.name}"
            } ?: "Default"
            defaultBuilderEntry = BuilderLibrary.Entry(newDefaultName, newDefaultBuilder)

            // Update the list in the combo
            val libraryBuilders = library.findBuildersFor(node)
            allBuilders.apply {
                clear()
                add(defaultBuilderEntry)
                addAll(libraryBuilders)
            }
            builderInput.refreshNames()
        }

        // Set currently selected builder
        val currentBuilder = node.builder
        if (builderEntry.builder != currentBuilder) {
            builderEntry = library.buildersByInstance[currentBuilder]
                ?: defaultBuilderEntry
        }
    }

    private fun renderHeaderText() {
        app.state.selectedObjects.run {
            when (size) {
                0 -> {}
                1 -> {
                    val obj = obj
                    when (obj) {
                        is SceneNode -> ImGui.text(obj.nodeClassName)
                        else -> ImGui.text(obj.toString())
                    }
                    if (obj?.isGenerated == true) {
                        ImGui.sameLine()
                        ImGui.text("(generated)")
                    }
                }
                else -> {}
            }
        }
    }

    private fun updateTagStr() {
        val obj = obj
        if (obj !is SceneNode) {
            tagStr = ""
            return
        }
        // This check exists because Java wrapper for ImGui requires String instances,
        // which are created every frame and use heap memory.
        // TODO: optimize string logic after ImGui wrapper fixes its strings
        //  (see https://github.com/SpaiR/imgui-java/issues/157)
        if (obj.node.tags != tags) {
            tags.clear()
            tags.addAll(obj.node.tags)
            tagStr = tags.joinToString(" ")
        }
    }

    /** Apply the modified values to the node. */
    private fun redrawIfNeeded() = redrawTimer.runAtInterval {
        if (originInput.dirty || sizeInput.dirty || startInput.dirty
            || rotationInput.dirty || colorInput.dirty) {
            obj?.let { obj ->
                when (obj) {
                    is SceneNode -> {
                        // Need to update size because "natural size" vector is not part of Node
                        obj.node.naturalSize = sizeInput.newValue
                        app.redrawNodes()
                    }
                    is SceneVoxelGroup -> app.redrawVoxels()
                }
            }
        }
    }

    /** Set ID on all inputs, to make them unique on the screen. */
    private fun updateInputIDs() {
        val id = obj?.id ?: return
        originInput.id = id
        sizeInput.id = id
        startInput.id = id
        tagsInput.id = id
        snapOriginInput.id = id
        rotationInput.id = id
        builderInput.id = id
        blueprintInput.id = id
        colorInput.id = id
    }

    companion object {
        /** Indicates that the default should be used. */
        private val nullBuilder = Builder<Node>()
    }
}