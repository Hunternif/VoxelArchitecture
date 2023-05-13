package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.deleteBlueprintNode
import hunternif.voxarch.editor.actions.setBlueprintNodeColor
import hunternif.voxarch.editor.actions.setBlueprintNodeStyle
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
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
    val app: EditorApp,
    val node: BlueprintNode,
    val padding: ImVec2,
) {
    private val styleMenu = GuiBlueprintNodeStyle(node)
    private val styleClassInput = GuiInputText("##${node.id}_classname", "class names")
    private val bpCombo by lazy {
        GuiCombo("##blueprint", app.state.blueprints)
    }
    private val colorInput = GuiInputColor("Set color", showInput = false)

    var showStyleClass: Boolean = node.extraStyleClass.isNotEmpty()

    fun render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8f, 8f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 2f)
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 4f, 2f)
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 4f)
        var width = max(20f, ImNodes.getNodeDimensionsX(node.id) - padding.x * 2f)

        //============================ Header =============================
        ImNodes.beginNodeTitleBar()
        // render default input on the same line as title
        node.inputs.firstOrNull()?.let {
            if (it.name == "in") {
                renderInputPin(it, false)
                ImGui.sameLine()
            }
        }
        // There is a bug in ImNodes that title color can't be changed
        if (node.isCustomColor) {
            drawColorSquare(node.color)
            ImGui.sameLine()
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
        if (node.domBuilder is DomRunBlueprint) {
            width = max(100f, width)
            ImGui.pushItemWidth(width)
            bpCombo.render(node.domBuilder.blueprint) {
                node.domBuilder.blueprint = it
            }
            ImGui.popItemWidth()
        }
        if (showStyleClass) {
            width = max(100f, width)
            ImGui.pushItemWidth(width)
            styleClassInput.render(node.extraStyleClass) {
                app.setBlueprintNodeStyle(node, it)
            }
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

        ImGui.popStyleVar(4)
    }


    fun renderContextMenu() {
        menu("Style...") {
            ImGui.pushItemWidth(150f)
            text("Style Rules")
            ImGui.separator()
            styleMenu.items.forEach { it.render() }
        }
        ImGui.separator()
        colorInput.render(node.color) {
            app.setBlueprintNodeColor(node, original, newValue)
        }
        menuCheck("Show class names", showStyleClass) {
            showStyleClass = !showStyleClass
        }
        menuItem("Delete node") {
            app.deleteBlueprintNode(node)
            ImGui.closeCurrentPopup()
        }
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