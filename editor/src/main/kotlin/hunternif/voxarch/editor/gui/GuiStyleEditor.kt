package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.AllStyleProperties
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.History
import hunternif.voxarch.editor.actions.setTextEditorActive
import hunternif.voxarch.editor.actions.updateStylesheetAndText
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import hunternif.voxarch.editor.blueprint.nodeFactoryByName
import hunternif.voxarch.editor.file.style.parseStylesheet
import hunternif.voxarch.editor.scene.KeyListener
import hunternif.voxarch.util.clamp
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.extension.texteditor.flag.TextEditorPaletteIndex
import imgui.extension.texteditor.flag.TextEditorPaletteIndex.*
import imgui.flag.ImGuiFocusedFlags
import imgui.internal.ImGui
import org.lwjgl.glfw.GLFW.*

class GuiStyleEditor(
    private val app: EditorApp,
) : KeyListener {
    private val editor = TextEditor()

    private var currentText: String = ""

    /** Whether this text input is active, i.e. has a typing cursor */
    private var isActive: Boolean = false

    private val applyTimer = Timer(0.1)
    private var isDirty: Boolean = false

    // Only update stylesheet after user stopped typing for X seconds
    private var lastTypeTime: Double = glfwGetTime()
    private val stopTypingDelaySecs: Double = 0.5
    private val stoppedTyping: Boolean
        get() = glfwGetTime() - lastTypeTime > stopTypingDelaySecs

    // Multiple undo/redo
    /** Counts how many symbols were typed per 'undo' block.
     * (Must contain at least 1 item to work properly). */
    private var typingHistory = History<Int>().apply { append(0) }
    private var typingStreak: Int = 0
    private var isUndoing: Boolean = false


    /** Replaces text in the editor with [newText] */
    fun loadText(newText: String) {
        currentText = newText
        editor.text = newText
//        println("updated text")
        typingHistory.clear()
        typingHistory.append(0)
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

        handleTyping()
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

    private fun handleTyping() {
        if (isActive && editor.isTextChanged) {
            lastTypeTime = glfwGetTime()
            isDirty = true

            val io = ImGui.getIO()
            val ctrl = io.keyCtrl
            val shift = io.keyShift
            when {
                ctrl && shift && io.getKeysDown(GLFW_KEY_Z) -> {}
                ctrl && io.getKeysDown(GLFW_KEY_Y) -> {}
                ctrl && io.getKeysDown(GLFW_KEY_Z) -> {}
                else -> typingStreak++
            }
        }
        if (stoppedTyping) {
            commitTypingStreak()
        }
    }

    override fun onKeyPress(key: Int, action: Int, mods: Int) {
        if (action != GLFW_PRESS) return

        val ctrl = (mods and GLFW_MOD_CONTROL != 0)
        val shift = (mods and GLFW_MOD_SHIFT != 0)

        if (isActive) {
            when {
                ctrl && shift && key == GLFW_KEY_Z -> {
                    commitTypingStreak()
                    isUndoing = true
                    // redo n steps, because TextEditor doesn't know Ctrl + Shift + Z
                    val redoCount = typingHistory.moveForward() ?: 0
//                    println("redo $redoCount")
                    editor.redo(redoCount)
                }
                ctrl && key == GLFW_KEY_Y -> {
                    commitTypingStreak()
                    isUndoing = true
                    // redo (n - 1) steps, because 1 was already redone
                    val redoCount = typingHistory.moveForward()?.minus(1) ?: 0
//                    println("redo $redoCount")
                    editor.redo(redoCount)
                }
                ctrl && key == GLFW_KEY_Z -> {
                    commitTypingStreak()
                    isUndoing = true
                    // undo (n - 1) steps, because 1 was already undone
                    val undoCount = typingHistory.moveBack()?.minus(1) ?: 0
//                    println("undo $undoCount")
                    editor.undo(undoCount)
                }
                key == GLFW_KEY_KP_ENTER -> {
                    // TextEditor doesn't know this key by default
                    insertText("\n")
                }
            }
        }
    }

    private fun commitTypingStreak() {
        if (typingStreak > 0) {
//            println("committed $typingStreak steps")
            typingHistory.append(typingStreak)
            typingStreak = 0
        }
    }

    /** Insert text and also add it to history */
    private fun insertText(text: String) {
        // TextEditor.insertText() doesn't write to history, but paste() does.
        val prevClipboard = ImGui.getClipboardText()
        ImGui.setClipboardText(text)
        editor.paste()
        typingStreak++
        ImGui.setClipboardText(prevClipboard)
    }
}