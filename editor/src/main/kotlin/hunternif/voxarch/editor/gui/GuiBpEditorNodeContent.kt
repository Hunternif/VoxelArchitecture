package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.util.ColorRGBa
import imgui.ImColor
import imgui.ImGui
import imgui.ImVec2
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesColorStyle
import imgui.extension.imnodes.flag.ImNodesPinShape
import imgui.flag.ImGuiStyleVar
import kotlin.math.max

class GuiBpEditorNodeContent(
    val node: BlueprintNode,
    val padding: ImVec2,
) {
    private val styleMenu = GuiBlueprintNodeStyle(node)
    private val styleClassInput = GuiInputText("##${node.id}_classname", "class name")

    fun render() {
        val width = max(20f, ImNodes.getNodeDimensionsX(node.id) - padding.x * 2f)

        //============================ Header =============================
        ImNodes.beginNodeTitleBar()
        // render default input on the same line as title
        node.inputs.firstOrNull()?.let {
            if (it.name == "in") {
                renderInputPin(it, false)
                ImGui.sameLine()
            }
        }
        ImGui.text(node.name)
        // render default output on the same line as title
        node.outputs.firstOrNull()?.let {
            if (it.name == "out") {
                ImGui.sameLine()
                renderOutputPin(it, 0f, false)
            }
        }
        ImNodes.endNodeTitleBar()

        //========================= Extra inputs ==========================
        for (slot in node.inputs) {
            if (slot.name == "in") continue
            renderInputPin(slot)
        }

        //============================= Body ==============================
        if (node.name != "Start") {
            ImGui.pushItemWidth(width)
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 2f)
            styleClassInput.render(node.extraStyleClass)
            ImGui.popStyleVar()
            ImGui.popItemWidth()
        }
        styleMenu.items.forEach { item ->
            if (item.enabled) {
                ImGui.bulletText(item.stringRepr)
            }
        }

        //========================= Extra outputs =========================
        for (slot in node.outputs) {
            if (slot.name == "out") continue
            renderOutputPin(slot, width)
        }
    }

    fun renderStyleMenu() {
        ImGui.pushItemWidth(150f)
        text("Style Rules")
        ImGui.separator()
        styleMenu.items.forEach { it.render() }
    }

    private fun renderInputPin(slot: BlueprintSlot.In, named: Boolean = true) {
        pushNodesColorStyle(ImNodesColorStyle.Pin, pinColor(slot))
        ImNodes.beginInputAttribute(slot.id, ImNodesPinShape.CircleFilled)
        if (named) text(slot.name) else ImGui.text("")
        ImNodes.endInputAttribute()
        ImNodes.popColorStyle()
    }

    private fun renderOutputPin(
        slot: BlueprintSlot.Out, width: Float, named: Boolean = true) {
        pushNodesColorStyle(ImNodesColorStyle.Pin, pinColor(slot))
        ImNodes.beginOutputAttribute(slot.id, ImNodesPinShape.CircleFilled)
        if (named) text(slot.name, Align.RIGHT, width) else ImGui.text("")
        ImNodes.endOutputAttribute()
        ImNodes.popColorStyle()
    }

    private fun pinColor(slot: BlueprintSlot) =
        if (slot.links.isEmpty()) Colors.emptySlot else Colors.filledSlot

    private fun pushNodesColorStyle(imGuiCol: Int, color: ColorRGBa) {
        color.run { ImNodes.pushColorStyle(imGuiCol, ImColor.floatToColor(r, g, b, a)) }
    }
}