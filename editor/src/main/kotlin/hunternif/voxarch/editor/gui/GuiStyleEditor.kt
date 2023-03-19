package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorApp
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition

class GuiStyleEditor(
    private val app: EditorApp,
) {
    private val editor = TextEditor()
    private var text: String = ""

    var stylesheet: Stylesheet = Stylesheet()
        set(value) {
            if (field != value) {
                text = value.toString()
                field = value
                editor.textLines = text.split('\n').toTypedArray()
            }
        }

    fun init() {
        val lang = TextEditorLanguageDefinition.c()
        lang.setKeywords(arrayOf("inherit"))
        editor.setLanguageDefinition(lang)
        editor.setShowWhitespaces(false)
        editor.tabSize = 2
        editor.palette = editor.darkPalette
    }

    fun render() {
        stylesheet = app.state.stylesheet
        editor.render("TextEditor")
    }
}