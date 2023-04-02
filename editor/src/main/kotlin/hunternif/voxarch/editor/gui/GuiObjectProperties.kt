package hunternif.voxarch.editor.gui

import hunternif.voxarch.builder.Builder
import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.builder.BuilderLibrary
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
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
    private val gui: GuiBase,
) {
    private val originInput = GuiInputVec3("origin")
    private val sizeInput = GuiInputVec3("voxel size", min = 1f)
    private val startInput = GuiInputVec3("start")
    private val snapOriginInput = GuiCombo("snap origin", *SnapOrigin.values())
    private val rotationInput = GuiInputFloat("rotation", speed = 5f, min = -360f, max = 360f)
    //TODO: update list of builders based on node type
    private val builderInput by lazy {
        GuiCombo("builder", app.state.builderLibrary.allBuilders)
    }

    // Update timer
    private val nodeTimer = Timer(0.02)
    private val builderTimer = Timer(0.1)
    private val blueprintsTimer = Timer(0.02)
    private val redrawTimer = Timer(0.02)

    // Currently selected items
    private var obj: SceneObject? = null
    private val voxGroupSize: Vec3 = Vec3(0, 0, 0)

    /** Default entry that indicates which builder will be assigned by BuilderConfig */
    private var defaultBuilderEntry = BuilderLibrary.Entry("Default", nullBuilder)
    private var builderEntry: BuilderLibrary.Entry = defaultBuilderEntry

    private val curBlueprints = mutableListOf<Blueprint>()

    fun render() {
        checkSelectedNodes()
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

        sizeInput.render(node.naturalSize) {
            app.transformNodeNaturalSize(sceneNode, original, newValue)
        }

        startInput.render(node.start) {
            app.transformNodeStart(sceneNode, original, newValue)
        }

        snapOriginInput.render(sceneNode.snapOrigin) {
            app.transformNodeSnapOrigin(sceneNode, it)
        }

        rotationInput.render(node.rotationY.toFloat(), onUpdated = {
            node.rotationY = newValue.toDouble()
        }, onEditFinished = {
            app.transformNodeRotation(sceneNode, original.toDouble(), newValue.toDouble())
        })

        checkNodeBuilder(sceneNode.node)
        builderInput.render(builderEntry) {
            //TODO: allow executing various builders regardless of exact node type
            builderEntry = it
            val newBuilder = if (it == defaultBuilderEntry) null else it.builder
            app.setNodeBuilder(sceneNode, newBuilder)
        }

        ImGui.separator()
        ImGui.text("Blueprints")
        button("New blueprint...") {
            app.addNewBlueprint(sceneNode)
        }
        renderBlueprintTable(sceneNode)
    }

    private fun renderBlueprintTable(sceneNode: SceneNode) {
        updateCurrentBlueprints(sceneNode)
        if (ImGui.beginTable("blueprints_table", 2, ImGuiTableFlags.PadOuterX)) {
            ImGui.tableSetupColumn("name")
            // it's not actually 10px wide, selectable makes it wider
            ImGui.tableSetupColumn("remove", ImGuiTableColumnFlags.WidthFixed, 10f)
            curBlueprints.forEachIndexed { i, blue ->
                ImGui.tableNextRow()
                ImGui.tableNextColumn()
                if (ImGui.selectable(memoStrWithIndex(blue.name, i),
                        app.state.selectedBlueprint == blue)
                ) {
                    app.selectBlueprint(blue)
                }
                ImGui.tableNextColumn()
                gui.inlineIconButton(memoStrWithIndex(FontAwesomeIcons.Times, i)) {
                    app.removeBlueprint(sceneNode, blue)
                }
            }
            ImGui.endTable()
        }
    }

    private fun updateCurrentBlueprints(sceneNode: SceneNode) = blueprintsTimer.runAtInterval {
        curBlueprints.clear()
        curBlueprints.addAll(sceneNode.blueprints)
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

    /** Check which nodes are currently selected, and update the state of gui */
    private fun checkSelectedNodes() = nodeTimer.runAtInterval {
        app.state.selectedObjects.run {
            obj = when (size) {
                0 -> null
                1 -> first()
                else -> null
            }
        }
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
            builderInput.values = listOf(defaultBuilderEntry) + library.allBuilders
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
                else -> {
                    ImGui.text(size.toString())
                    ImGui.sameLine()
                    ImGui.text("nodes")
                }
            }
        }
    }

    /** Apply the modified values to the node. */
    private fun redrawIfNeeded() = redrawTimer.runAtInterval {
        if (originInput.dirty || sizeInput.dirty || startInput.dirty
            || rotationInput.dirty) {
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

    companion object {
        /** Indicates that the default should be used. */
        private val nullBuilder = Builder<Node>()
    }
}