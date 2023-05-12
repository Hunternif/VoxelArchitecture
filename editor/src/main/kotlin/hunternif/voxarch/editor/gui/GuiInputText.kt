package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.type.ImString
import org.lwjgl.glfw.GLFW

class GuiInputText(
    override val label: String,
    val hint: String? = null,
) : GuiInput() {
    /** Data storage that ImGui understands */
    @PublishedApi internal val data = ImString()

    @PublishedApi internal var isDirty = false

    // Only call update after user stopped typing for X seconds
    @PublishedApi internal var lastTypeTime: Double = GLFW.glfwGetTime()
    @PublishedApi internal val stopTypingDelaySecs: Double = 0.5
    @PublishedApi internal val stoppedTyping: Boolean
        get() = GLFW.glfwGetTime() - lastTypeTime > stopTypingDelaySecs

    inline fun render(
        initialValue: String,
        crossinline onUpdate: (newValue: String) -> Unit = {}
    ) {
        if (!isDirty) data.set(initialValue)
        if (renderText()) {
            lastTypeTime = GLFW.glfwGetTime()
            isDirty = true
        }
        if (isDirty && (stoppedTyping || !ImGui.isItemActive())) {
            isDirty = false
            onUpdate(data.get())
        }
    }

    @PublishedApi
    internal fun renderText(): Boolean {
        return if (hint != null) ImGui.inputTextWithHint(label, hint, data)
        else ImGui.inputText(label, data)
    }
}