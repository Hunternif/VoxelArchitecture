package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.AllStyleProperties
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import hunternif.voxarch.editor.blueprint.nodeFactoryByName
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.extension.texteditor.flag.TextEditorPaletteIndex
import imgui.extension.texteditor.flag.TextEditorPaletteIndex.*

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
        val lang = TextEditorLanguageDefinition.glsl()

        // TextEditor supports a few color categories:
        // - keywords
        // - identifiers
        // - preprocessor identifiers

        val keywords = mutableSetOf(
            "inherit",
        )
        lang.setKeywords(keywords.toTypedArray())

        val identifiers = mutableMapOf<String, String>()
        // Treat Node & DOM types as "identifiers":
        nodeFactoryByName.keys.forEach { identifiers[it] = "Node class '$it'" }
        domBuilderFactoryByName.values.forEach {
            val type = it().javaClass.simpleName
            identifiers[type] = "DOM builder class '$type'"
        }
        lang.setIdentifiers(identifiers)

        val preprocIds = mutableMapOf<String, String>()
        // Treat property names as "preprocessor identifiers":
        AllStyleProperties.forEach {
            preprocIds[it.name] = "property '${it.name}'"
        }
        lang.setPreprocIdentifiers(preprocIds)

        val regexes = mapOf(
            "#.*" to Comment,
            "\\\"[^\\\"]*\\\"|\\\'[^\\\']*\\\'" to TextEditorPaletteIndex.String,
            "[0-9]+(\\.[0-9]+)?" to Number,
            "\\.[A-Za-z_][A-Za-z0-9_]*" to Preprocessor, // style class names in selector
            "[A-Za-z_][A-Za-z0-9_-]*" to Identifier, // Node type names in selectors
            "[\\{\\}\\:\\;]" to Comment,
            "[\\[\\]\\%\\*\\(\\)\\-\\+\\~\\>\\/\\,\\.]" to Punctuation
        )
        lang.setTokenRegexStrings(regexes)

        editor.setLanguageDefinition(lang)
        editor.setShowWhitespaces(false)
        editor.tabSize = 2
        editor.palette = editor.darkPalette.clone().also {
            it[Comment] = Colors.styleComment.hexABGR
            it[MultiLineComment] = Colors.styleComment.hexABGR
            it[LineNumber] = Colors.styleLineNumber.hexABGR
            it[PreprocIdentifier] = Colors.styleProperty.hexABGR
            it[KnownIdentifier] = Colors.styleTypeName.hexABGR
            it[Preprocessor] = Colors.styleClassName.hexABGR
            it[Identifier] = Colors.styleValue.hexABGR
            it[Punctuation] = Colors.styleValue.hexABGR
            it[Keyword] = Colors.styleKeyword.hexABGR
            it[Number] = Colors.styleValue.hexABGR
            it[TextEditorPaletteIndex.String] = Colors.styleString.hexABGR
        }
    }

    fun render() {
        stylesheet = app.state.stylesheet
        editor.render("TextEditor")
    }
}