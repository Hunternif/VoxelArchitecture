package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.builder.createGeneratorByName
import hunternif.voxarch.editor.util.ColorRGBa
import imgui.ImColor
import imgui.ImGui
import imgui.ImVec2
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesColorStyle
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation
import imgui.extension.imnodes.flag.ImNodesPinShape
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiStyleVar
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW

class GuiBlueprintEditor(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    private var hoveredLinkID: Int = -1
    private var hoveredNodeID: Int = -1

    private val editorPos = ImVec2()
    private val clickPos = ImVec2()
    private var isEditorHovered = false
    /** A newly created node will be linked to this slot */
    private var lastOutSlot: BlueprintSlot.Out? = null

    private val LINK = ImInt()
    private val SLOT_A = ImInt()
    private val SLOT_B = ImInt()

    private val titleInput = GuiInputText("title")

    fun init() {
        ImNodes.createContext()
    }

    fun render() {
        app.state.selectedBlueprint?.run {
            renderEditor()
            installControls()
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10f, 10f)
            overlay("toolbar", Corner.TOP_LEFT, padding = 0f, bgAlpha = 0f) {
                gui.iconButton(FontAwesomeIcons.Cog, font = gui.fontMediumIcons) {
                    ImGui.openPopup("settings_context")
                }
                popup("settings_context") {
                    text("Blueprint settings")
                    ImGui.separator()
                    titleInput.render(name) { name = it }
                }
            }
            popup("node_context") {
                val targetNode = nodeIDs.map[hoveredNodeID]
                disabled(targetNode == start) {
                    menuItem("Delete node") {
                        targetNode?.let { app.deleteBlueprintNode(it) }
                        ImGui.closeCurrentPopup()
                    }
                }
            }
            popup("link_context") {
                menuItem("Unlink") {
                    val targetLink = linkIDs.map[hoveredLinkID]
                    targetLink?.let { app.unlinkBlueprintLink(it) }
                    ImGui.closeCurrentPopup()
                }
            }
            popup("node_editor_context") {
                menu("Add..") {
                    text("Generator:")
                    listbox("##generator_type") {
                        app.state.generatorNames.forEach { name ->
                            selectable(name) {
                                addNodeWithGenerator(name, clickPos)
                                ImGui.closeCurrentPopup()
                            }
                        }
                    }
                }
            }
            ImGui.popStyleVar()
        }
    }

    private fun Blueprint.renderEditor() {
        ImNodes.beginNodeEditor()
        ImNodes.getStyle().apply {
            pinCircleRadius = 4f
            nodeCornerRounding = 2f
            setNodePadding(8f, 5f)
        }
        ImGui.getWindowPos(editorPos)

        nodes.forEach { node ->
            ImNodes.beginNode(node.id)

            ImNodes.beginNodeTitleBar()
            val width = calcTextSize(node.name).x
            ImGui.text(node.name)
            ImNodes.endNodeTitleBar()

            node.inputs.forEach {
                pushNodesColorStyle(ImNodesColorStyle.Pin, pinColor(it))
                ImNodes.beginInputAttribute(it.id, ImNodesPinShape.CircleFilled)
                text(it.name)
                ImNodes.endInputAttribute()
                ImNodes.popColorStyle()
            }
            node.outputs.forEach {
                pushNodesColorStyle(ImNodesColorStyle.Pin, pinColor(it))
                ImNodes.beginOutputAttribute(it.id, ImNodesPinShape.CircleFilled)
                text(it.name, Align.RIGHT, width)
                ImNodes.endOutputAttribute()
                ImNodes.popColorStyle()
            }
            ImNodes.endNode()

            node.x = ImNodes.getNodeGridSpacePosX(node.id)
            node.y = ImNodes.getNodeGridSpacePosY(node.id)
        }
        links.forEach { link ->
            ImNodes.link(link.id, link.from.id, link.to.id)
        }
        val pos = ImVec2()
        ImNodes.getNodeGridSpacePos(start.id, pos)

        isEditorHovered = ImNodes.isEditorHovered()

        ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight)
        ImNodes.endNodeEditor()
    }

    private fun pinColor(slot: BlueprintSlot) =
        if (slot.links.isEmpty()) Colors.emptySlot else Colors.filledSlot

    private fun pushNodesColorStyle(imGuiCol: Int, color: ColorRGBa) {
        color.run { ImNodes.pushColorStyle(imGuiCol, ImColor.floatToColor(r, g, b, a)) }
    }

    /** Implements a workaround to find this value from the start node
     * because EditorContextGetPanning() is missing from the Java lib */
    private fun Blueprint.getPanning(): ImVec2 {
        val startScreenPos = ImVec2()
        ImNodes.getNodeEditorSpacePos(start.id, startScreenPos)
        val startGridPos = ImVec2()
        ImNodes.getNodeGridSpacePos(start.id, startGridPos)
        startScreenPos.apply {
            x -= startGridPos.x
            y -= startGridPos.y
        }
        return startScreenPos
    }

    private fun Blueprint.installControls() {
        if (ImNodes.isLinkStarted(SLOT_A)) {
            lastOutSlot = slotIDs.map[SLOT_A.get()] as? BlueprintSlot.Out
        }

        if (ImNodes.isLinkCreated(SLOT_A, SLOT_B)) {
            lastOutSlot = null
            val from = slotIDs.map[SLOT_A.get()]
            val to = slotIDs.map[SLOT_B.get()]
            if (from is BlueprintSlot.Out && to is BlueprintSlot.In) {
                app.linkBlueprintSlots(from, to)
            }
        }

        if (ImNodes.isLinkDropped(LINK, false)) {
            storeClickPos()
            ImGui.openPopup("node_editor_context")
        }

        if (ImNodes.isLinkDestroyed(LINK)) {
            val link = linkIDs.map[LINK.get()]
            link?.let { app.unlinkBlueprintLink(it) }
        }

        if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
            hoveredLinkID = ImNodes.getHoveredLink()
            hoveredNodeID = ImNodes.getHoveredNode()
            storeClickPos()
            if (hoveredLinkID != -1) {
                ImGui.openPopup("link_context")
            } else if (hoveredNodeID != -1) {
                ImGui.openPopup("node_context")
            } else if (isEditorHovered) {
                ImGui.openPopup("node_editor_context")
            }
        }

        if (ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE, false)) {
            deleteSelected()
        }
    }

    private fun Blueprint.deleteSelected() {
        val selectedNodeIDs = IntArray(ImNodes.numSelectedNodes())
        ImNodes.getSelectedNodes(selectedNodeIDs)
        val selectedLinkIDs = IntArray(ImNodes.numSelectedLinks())
        ImNodes.getSelectedLinks(selectedLinkIDs)
        val nodes = selectedNodeIDs.map { nodeIDs.map[it] }.filterNotNull()
        val links = selectedLinkIDs.map { linkIDs.map[it] }.filterNotNull()
        app.deleteBlueprintParts(nodes, links)
    }

    private fun Blueprint.storeClickPos() {
        val panPos = getPanning()
        clickPos.x = ImGui.getMousePosX() - editorPos.x - panPos.x
        clickPos.y = ImGui.getMousePosY() - editorPos.y - panPos.y
    }

    private fun Blueprint.addNodeWithGenerator(classname: String, pos: ImVec2) {
        val generator = app.state.createGeneratorByName(classname)
        generator?.let {
            app.newBlueprintNode(this, it.javaClass.simpleName, it, pos.x, pos.y, lastOutSlot)
        }
    }
}