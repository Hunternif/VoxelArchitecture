package hunternif.voxarch.editor.gui

/**
 * Base class for inputs
 */
abstract class GuiInput {
    abstract val label: String

    var id: Int = 0
        set(value) {
            field = value
            labelForImgui = "$label##$id"
        }
    /** Used for differentiating between different inputs with the same label */
    @PublishedApi internal var labelForImgui = "$label##$id"
}