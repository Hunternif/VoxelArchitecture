package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.updateStylesheetAndText
import hunternif.voxarch.editor.blueprint.DomBuilderFactory
import hunternif.voxarch.editor.blueprint.NodeFactory
import hunternif.voxarch.editor.blueprint.styleEditorStyleProperties
import hunternif.voxarch.editor.file.style.StyleParser
import hunternif.voxarch.util.clamp
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.extension.texteditor.flag.TextEditorPaletteIndex
import imgui.extension.texteditor.flag.TextEditorPaletteIndex.*

class GuiStylesheetEditor(
    app: EditorApp,
) : GuiStyleEditor(app, Mode.STYLESHEET) {

    override fun applyStyleRules(parsed: StyleParser.StyleParseResult) {
        val newSheet = Stylesheet.fromRules(parsed.rules)
        app.updateStylesheetAndText(newSheet, currentText)
    }
}

abstract class GuiStyleEditor(
    app: EditorApp,
    private val mode: Mode,
) : GuiTextEditor(app) {
    /** Determines parsing rules for error highlighting */
    enum class Mode {
        /** Complete stylesheet for the project */
        STYLESHEET,
        /** Only declarations for a single rule */
        DECLARATIONS,
    }

    private val parser = StyleParser()

    fun init() {
        editor.apply {
            setLanguageDefinition(styleLanguage)
            setShowWhitespaces(false)
            tabSize = 2
            palette = darkPalette.clone().also {
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
    }

    override fun onTextChanged() {
        val totalLines = editor.totalLines

        val parsed = when (mode) {
            Mode.STYLESHEET -> parser.parseStylesheet(currentText)
            Mode.DECLARATIONS -> parser.parseDeclarations(currentText)
        }

        val errorMap = parsed.errors.associate {
            // Sometimes the error is reported on the next line after EOF
            it.line.clamp(1, totalLines) to it.msg
        }
        editor.setErrorMarkers(errorMap)

        applyStyleRules(parsed)
    }

    abstract fun applyStyleRules(parsed: StyleParser.StyleParseResult)

    companion object {
        val styleLanguage: TextEditorLanguageDefinition by lazy {
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
            NodeFactory.allNodeTypes.forEach {
                identifiers[it.name] = "Node class '${it.name}'"
            }
            DomBuilderFactory.allDomBuilders.forEach {
                identifiers[it.className] = "DOM builder class '${it.className}'"
            }
            lang.setIdentifiers(identifiers)

            val preprocIds = mutableMapOf<String, String>()
            // Treat property names as "preprocessor identifiers":
            styleEditorStyleProperties.forEach {
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
            lang
        }
    }
}