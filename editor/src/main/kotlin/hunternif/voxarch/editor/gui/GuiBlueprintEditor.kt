package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.deleteBlueprintNode
import hunternif.voxarch.editor.actions.linkBlueprintSlots
import hunternif.voxarch.editor.actions.newBlueprintNode
import hunternif.voxarch.editor.actions.unlinkBlueprintSlot
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.generator.TurretGenerator
import imgui.ImGui
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesAttributeFlags
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation
import imgui.extension.imnodes.flag.ImNodesPinShape
import imgui.flag.ImGuiMouseButton
import imgui.type.ImInt
import kotlin.math.max

class GuiBlueprintEditor(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    private val minWidth = 100f

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
            ImNodes.pushAttributeFlag(ImNodesAttributeFlags.EnableLinkDetachWithDragClick)
            val editorPos = ImGui.getWindowPos()

            nodes.forEach { node ->
                ImNodes.beginNode(node.id)
                ImNodes.beginNodeTitleBar()
                node.x = ImNodes.getNodeEditorSpacePosX(node.id)
                node.y = ImNodes.getNodeEditorSpacePosY(node.id)
                val width = max(minWidth, calcTextSize(node.name).x)
                ImGui.text(node.name)
                ImNodes.endNodeTitleBar()
                node.inputs.forEach {
                    ImNodes.beginInputAttribute(it.id, pinShape(it))
                    text(it.name, Align.LEFT)
                    ImNodes.endInputAttribute()
                }
                node.outputs.forEach {
                    ImNodes.beginOutputAttribute(it.id, pinShape(it))
                    text(it.name, Align.RIGHT, width)
                    ImNodes.endOutputAttribute()
                }
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
                    app.linkBlueprintSlots(from, to)
                }
            }

            if (ImNodes.isLinkDestroyed(LINK_A)) {
                val link = linkIDs.map[LINK_A.get()]
                link?.let { app.unlinkBlueprintSlot(it) }
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
                        targetNode?.let { app.deleteBlueprintNode(it) }
                        ImGui.closeCurrentPopup()
                    }
                    ImGui.endPopup()
                }
            }

            if (ImGui.beginPopup("node_editor_context")) {
                if (ImGui.button("Create New Node")) {
                    val x = ImGui.getMousePosX() - editorPos.x
                    val y = ImGui.getMousePosY() - editorPos.y
                    app.newBlueprintNode(this, TurretGenerator(), x, y)
                    ImGui.closeCurrentPopup()
                }
                ImGui.endPopup()
            }
        }
    }

    private fun pinShape(slot: BlueprintSlot) = when (slot.link) {
        null -> ImNodesPinShape.Triangle
        else -> ImNodesPinShape.TriangleFilled
    }

    private fun checkSelectedBlueprint() = loadBPTimer.runAtInterval {
        blueprint = app.state.selectedBlueprint
    }
}