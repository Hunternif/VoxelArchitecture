package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import imgui.extension.imnodes.ImNodes

class GuiBlueprintEditor(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    fun init() {
        ImNodes.createContext()
    }

    fun render() {
        ImNodes.beginNodeEditor()
        ImNodes.endNodeEditor()
    }
}