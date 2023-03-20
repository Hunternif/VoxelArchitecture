package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.AllStyleProperties
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.setTextEditorActive
import hunternif.voxarch.editor.actions.updateStylesheetAndText
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import hunternif.voxarch.editor.blueprint.nodeFactoryByName
import hunternif.voxarch.editor.file.style.parseStylesheet
import hunternif.voxarch.util.clamp
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.extension.texteditor.flag.TextEditorPaletteIndex
import imgui.extension.texteditor.flag.TextEditorPaletteIndex.*
import imgui.flag.ImGuiFocusedFlags
import imgui.internal.ImGui
import org.lwjgl.glfw.GLFW

class GuiStyleEditor(
    private val app: EditorApp,
) {
    private val editor = TextEditor()

    /** This is only used to speed up comparison when loading text. */
    private var currentStyle = Stylesheet()
    private var currentText: String = ""

    /** Whether this text input is active, i.e. has a typing cursor */
    private var isActive: Boolean = false

    private val applyTimer = Timer(0.1)
    private var isDirty: Boolean = false

    // Only update stylesheet after user stopped typing for X seconds
    private var lastTypeTime: Double = GLFW.glfwGetTime()
    private val stopTypingDelaySecs: Double = 0.5
    private val stoppedTyping: Boolean
        get() = GLFW.glfwGetTime() - lastTypeTime > stopTypingDelaySecs

    /** Replaces text in the editor with [newText] */
    fun loadText(newStyle: Stylesheet, newText: String) {
        if (newStyle != currentStyle) {
            currentStyle = newStyle
            currentText = newText
            editor.text = newText
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
        isActive = ImGui.isWindowFocused(ImGuiFocusedFlags.ChildWindows)
        app.setTextEditorActive(isActive)

        editor.render("TextEditor")

        updateTyping()
        applyTimer.runAtInterval { applyStylesheet() }
    }

    private fun applyStylesheet() {
        if (isDirty && stoppedTyping) {
            currentText = editor.text
            val totalLines = editor.totalLines

            val parsed = parseStylesheet(currentText)

            val errorMap = parsed.errors.associate {
                // Sometimes the error is reported on the next line after EOF
                it.line.clamp(1, totalLines) to it.msg
            }
            editor.setErrorMarkers(errorMap)

            val newSheet = Stylesheet.fromRules(parsed.rules)
            app.updateStylesheetAndText(newSheet, currentText)
            isDirty = false
        }
    }

    private fun updateTyping() {
        if (editor.isTextChanged) isDirty = true
        if (isActive && editor.isTextChanged) {
            lastTypeTime = GLFW.glfwGetTime()
        }
    }
}