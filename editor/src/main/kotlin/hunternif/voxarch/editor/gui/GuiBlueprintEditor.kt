package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.linkBlueprintNodes
import hunternif.voxarch.editor.actions.newBlueprintNode
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.generator.TurretGenerator
import imgui.ImGui
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation
import imgui.extension.imnodes.flag.ImNodesPinShape
import imgui.flag.ImGuiMouseButton
import imgui.type.ImInt

class GuiBlueprintEditor(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    private var blueprint: Blueprint? = null

    private val LINK_A = ImInt()
    private val LINK_B = ImInt()

    private val loadBPTimer = Timer(0.2)

    fun init() {
        ImNodes.createContext()
    }

    fun render() {
        checkSelectedBlueprint()

        blueprint?.run {
            ImNodes.beginNodeEditor()
            nodes.forEach { node ->
                ImNodes.beginNode(node.id)
                ImNodes.beginNodeTitleBar()
                ImGui.text(node.name)
                ImNodes.endNodeTitleBar()
                ImNodes.beginInputAttribute(node.input.id, pinShape(node.input))
                ImGui.text("In")
                ImNodes.endInputAttribute()
                ImGui.sameLine()
                ImNodes.beginOutputAttribute(node.output.id, pinShape(node.output))
                ImGui.text("Out")
                ImNodes.endOutputAttribute()
                ImNodes.endNode()
            }
            links.forEach { link ->
                ImNodes.link(link.id, link.from.id, link.to.id)
            }

            val isEditorHovered = ImNodes.isEditorHovered()

            ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight)
            ImNodes.endNodeEditor()

            if (ImNodes.isLinkCreated(LINK_A, LINK_B)) {
                val from = slotIDs.map[LINK_A.get()]
                val to = slotIDs.map[LINK_B.get()]
                if (from is BlueprintSlot.Out && to is BlueprintSlot.In) {
                    app.linkBlueprintNodes(from, to)
                }
            }

            if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                val hoveredNode = ImNodes.getHoveredNode()
                if (hoveredNode != -1) {
                    ImGui.openPopup("node_context")
                    ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), hoveredNode)
                } else if (isEditorHovered) {
                    ImGui.openPopup("node_editor_context")
                }
            }

            if (ImGui.isPopupOpen("node_context")) {
                val targetNodeID = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"))
                if (ImGui.beginPopup("node_context")) {
                    button("Delete node") {
                        val targetNode = nodeIDs.map[targetNodeID]
                        targetNode?.let { removeNode(it) }
                        ImGui.closeCurrentPopup()
                    }
                    ImGui.endPopup()
                }
            }

            if (ImGui.beginPopup("node_editor_context")) {
                if (ImGui.button("Create New Node")) {
                    val node = app.newBlueprintNode(this, TurretGenerator())
                    ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                    ImGui.closeCurrentPopup()
                }
                ImGui.endPopup()
            }
        }
    }

    private fun pinShape(slot: BlueprintSlot) = when (slot.link) {
        null -> ImNodesPinShape.Circle
        else -> ImNodesPinShape.CircleFilled
    }

    private fun checkSelectedBlueprint() = loadBPTimer.runAtInterval {
        blueprint = app.state.selectedBlueprint
    }
}