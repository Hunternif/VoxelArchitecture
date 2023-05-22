package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.util.ColorRGBa
import imgui.ImColor
import imgui.ImGui
import imgui.ImVec2
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesColorStyle
import imgui.extension.imnodes.flag.ImNodesPinShape
import imgui.flag.ImGuiStyleVar
import kotlin.math.max

class GuiBlueprintEditorNodeContent(
    val app: EditorApp,
    val node: BlueprintNode,
    val padding: ImVec2,
) {
    private val styleGuiAsInputs by lazy { GuiBlueprintNodeStyleAsInputs(app, node) }
    private val styleGuiAsText by lazy { GuiBlueprintNodeStyleAsText(app, node) }
    private val styleClassInput by lazy { GuiInputText("##${node.id}_classname", "class names") }
    private val outSlotNameInput by lazy { GuiInputText("##${node.id}_out_slot_name", "slot name") }
    private val bpCombo by lazy {
        GuiCombo("##blueprint", app.state.blueprints)
    }
    private val colorInput = GuiInputColor("Set color", showInput = false)

    var showStyleClass: Boolean = node.extraStyleClass.isNotEmpty()

    /** Current node width */
    private var width: Float = 0f

    private val isStartNode = node === node.bp.start
    private val isDelegateNode = node.domBuilder is DomRunBlueprint
    private val isOutSlotNode = node.domBuilder is DomBlueprintOutSlot

    fun render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8f, 8f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 2f)
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 4f, 2f)
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 4f)
        width = max(20f, ImNodes.getNodeDimensionsX(node.id) - padding.x * 2f)

        //============================ Header =============================
        renderHeader()

        //========================= Extra inputs ==========================
        for (slot in node.inputs) {
            if (slot.name == "in") continue
            renderInputPin(slot)
        }

        //============================= Body ==============================
        when (node.domBuilder) {
            is DomRunBlueprint -> renderDelegateNodeBody(node.domBuilder)
            is DomBlueprintOutSlot -> renderOutSlotBody(node.domBuilder)
            else -> {
                renderStyleClassInput()
                renderStyleSummary()
                renderOutputSlots()
            }
        }

        ImGui.popStyleVar(4)
    }

    private fun renderHeader() {
        ImNodes.beginNodeTitleBar()
        // render default input on the same line as title
        node.inputs.firstOrNull()?.let {
            if (it.name == "in") {
                renderInputPin(it, "")
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
                renderOutputPin(it, 0f, "")
            }
        }
        ImNodes.endNodeTitleBar()
    }

    private fun renderStyleClassInput() {
        if (showStyleClass) {
            width = max(100f, width)
            withWidth(width) {
                styleClassInput.render(node.extraStyleClass) {
                    app.setBlueprintNodeClass(node, it)
                }
            }
        }
    }

    private fun renderStyleSummary() {
        node.rule.declarations.forEach {
            // break it down to prevent creating new Java strings
            ImGui.bulletText(it.property.name)
            ImGui.sameLine()
            ImGui.text(it.value.toString())
        }
    }

    private fun renderOutputSlots() {
        for (slot in node.outputs) {
            if (slot.name == "out") continue
            renderOutputPin(slot, width)
        }
    }

    private fun updateBlueprints() {
        // Check if any new blueprints were added to the library
        if (bpCombo.values.size != app.state.blueprints.size) {
            bpCombo.values = app.state.blueprints.toList()
        }
    }

    private fun renderDelegateNodeBody(domDelegate: DomRunBlueprint) {
        width = max(100f, width)
        withWidth(width) {
            updateBlueprints()
            bpCombo.render(domDelegate.blueprint) {
                app.setDelegateBlueprint(node, it)
            }
        }
        //====================== Out slots ========================
        renderOutputSlots()
        //======================= Footer ==========================
        disabled(domDelegate.isEmpty) {
            button("Navigate", width = width) {
                app.selectBlueprint(domDelegate.blueprint)
            }
        }
    }
    private fun renderOutSlotBody(domSlot: DomBlueprintOutSlot) {
        width = max(100f, width)
        withWidth(width) {
            outSlotNameInput.render(domSlot.slotName) {
                // TODO add action to change out slot name
            }
        }
    }


    fun renderContextMenu() {
        if (isStartNode) {
            disabled { text("Start node") }
        }

        if (!isStartNode && !isOutSlotNode && !isDelegateNode) {
            menu("Style...") {
                text("Style Rules")
                tabBar("style_tabs") {
                    tabItem("Inputs") {
                        childWindow("style_container", 250f, 300f) {
                            styleGuiAsInputs.render()
                        }
                    }
                    tabItem("Text") {
                        childWindow("style_container", 250f, 300f) {
                            styleGuiAsText.render()
                        }
                    }
                }
            }
        }
        ImGui.separator()
        colorInput.render(node.color) {
            app.setBlueprintNodeColor(node, original, newValue)
        }
        if (node.isCustomColor) {
            ImGui.sameLine()
            button("Reset") {
                app.setBlueprintNodeColor(node, node.color, BlueprintNode.defaultColor)
            }
        }
        if (!isOutSlotNode) {
            menuCheck("Show class names", showStyleClass) {
                showStyleClass = !showStyleClass
            }
        }
        if (!isStartNode) {
            menuItem("Delete node") {
                app.deleteBlueprintNode(node)
                ImGui.closeCurrentPopup()
            }
        }
    }

    private fun renderInputPin(slot: BlueprintSlot.In, name: String = slot.name) {
        pushNodesColorStyle(ImNodesColorStyle.Pin, pinColor(slot))
        ImNodes.beginInputAttribute(slot.id, ImNodesPinShape.CircleFilled)
        text(name)
        ImNodes.endInputAttribute()
        ImNodes.popColorStyle()
    }

    private fun renderOutputPin(
        slot: BlueprintSlot.Out, width: Float, name: String = slot.name) {
        pushNodesColorStyle(ImNodesColorStyle.Pin, pinColor(slot))
        ImNodes.beginOutputAttribute(slot.id, ImNodesPinShape.CircleFilled)
        if (name.isNotEmpty()) text(name, Align.RIGHT, width)
        else ImGui.text("")
        ImNodes.endOutputAttribute()
        ImNodes.popColorStyle()
    }

    private fun pinColor(slot: BlueprintSlot) =
        if (slot.links.isEmpty()) Colors.emptySlot else Colors.filledSlot

    private fun pushNodesColorStyle(imGuiCol: Int, color: ColorRGBa) {
        color.run { ImNodes.pushColorStyle(imGuiCol, ImColor.floatToColor(r, g, b, a)) }
    }
}