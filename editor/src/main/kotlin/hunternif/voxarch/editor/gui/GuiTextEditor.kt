package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.history.History
import hunternif.voxarch.editor.actions.saveProjectOrOpenDialogToSaveAs
import hunternif.voxarch.editor.actions.setTextEditorActive
import imgui.extension.texteditor.TextEditor
import imgui.flag.ImGuiFocusedFlags
import imgui.internal.ImGui
import org.lwjgl.glfw.GLFW.*

/**
 * General-purpose text editor.
 * Provides a few small improvements over [TextEditor].
 */
abstract class GuiTextEditor(
    protected val app: EditorApp,
) {
    protected val editor = TextEditor()

    var currentText: String = ""
        private set

    /** Whether this text input is active, i.e. has a typing cursor */
    var isActive: Boolean = false
        private set

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

    /** Set to true after typing '{', for auto-completing '{ }' */
    private var openBrace: Boolean = false


    /** Replaces text in the editor with [newText] */
    fun loadText(newText: String) {
        currentText = newText
        editor.text = newText
        typingHistory.clear()
        typingHistory.append(0)
    }

    fun render() {
        isActive = ImGui.isWindowFocused(ImGuiFocusedFlags.ChildWindows)
        app.setTextEditorActive(isActive)

        editor.render("TextEditor")

        handleTyping()
        if (isActive && ImGui.isAnyMouseDown()) { checkIfTextChanged() }
        applyTimer.runAtInterval { checkIfTextChanged(checkTyping = true) }
    }

    /** @param checkTyping if true, will first check if user stopped typing */
    private fun checkIfTextChanged(checkTyping: Boolean = false) {
        if (isDirty && (!checkTyping || stoppedTyping)) {
            currentText = editor.text
            isDirty = false
            onTextChanged()
        }
    }

    /** Called when the text is changed and should be applied */
    protected abstract fun onTextChanged()

    private fun handleTyping() {
        if (isActive) {
            val io = ImGui.getIO()
            val ctrl = io.keyCtrl
            val shift = io.keyShift

            when {
                ctrl && shift && ImGui.isKeyPressed(GLFW_KEY_Z, false) -> {
                    commitTypingStreak()
                    isUndoing = true
                    // redo n steps, because TextEditor doesn't know Ctrl + Shift + Z
                    val redoCount = typingHistory.moveForward() ?: 0
                    editor.redo(redoCount)
                }
                ctrl && ImGui.isKeyPressed(GLFW_KEY_Y, false) -> {
                    commitTypingStreak()
                    isUndoing = true
                    // redo (n - 1) steps, because 1 was already redone
                    val redoCount = typingHistory.moveForward()?.minus(1) ?: 0
                    editor.redo(redoCount)
                }
                ctrl && ImGui.isKeyPressed(GLFW_KEY_Z, false) -> {
                    commitTypingStreak()
                    isUndoing = true
                    // undo (n - 1) steps, because 1 was already undone
                    val undoCount = typingHistory.moveBack()?.minus(1) ?: 0
                    editor.undo(undoCount)
                }
                ctrl && ImGui.isKeyPressed(GLFW_KEY_S, false) -> {
                    checkIfTextChanged()
                    app.saveProjectOrOpenDialogToSaveAs()
                }
                ImGui.isKeyPressed(GLFW_KEY_KP_ENTER, false) -> {
                    // TextEditor doesn't know this key by default
                    insertText("\n")
                }
            }

            if (editor.isTextChanged) {
                lastTypeTime = glfwGetTime()
                isDirty = true
                if (editor.currentLineText.lastOrNull() == '{') {
                    openBrace = true
                }
                when {
                    ctrl && shift && io.getKeysDown(GLFW_KEY_Z) -> {}
                    ctrl && io.getKeysDown(GLFW_KEY_Y) -> {}
                    ctrl && io.getKeysDown(GLFW_KEY_Z) -> {}
                    else -> typingStreak++
                }
            }

            // auto-complete '{ }':
            // (the numpad 'Enter' key doesn't count as 'text changed')
            if (openBrace && (io.getKeysDown(GLFW_KEY_ENTER) || io.getKeysDown(GLFW_KEY_KP_ENTER))) {
                openBrace = false
                insertText("  \n}")
                editor.moveUp(1, false)
                editor.moveEnd(false)
            }
        }
        if (stoppedTyping) {
            commitTypingStreak()
        }
    }

    private fun commitTypingStreak() {
        if (typingStreak > 0) {
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