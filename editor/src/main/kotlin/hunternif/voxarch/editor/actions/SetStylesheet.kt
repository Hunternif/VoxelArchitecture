package hunternif.voxarch.editor.actions

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons

/**
 * Sets the stylesheet and stylesheet text.
 *
 * It's assumed that the stylesheet is the result of parsing the text.
 *
 * It's also assumed that Style Editor already contains this text.
 */
class SetStylesheet(
    private val stylesheet: Stylesheet,
    private val text: String,
) : HistoryAction("Update stylesheet", FontAwesomeIcons.FileCode) {
    private lateinit var oldSheet: Stylesheet
    private lateinit var oldText: String

    override fun invoke(app: EditorAppImpl) {
        if (!::oldSheet.isInitialized) {
            oldSheet = app.state.stylesheet
            oldText = app.state.stylesheetText
        }
        app.state.stylesheet = stylesheet
        app.state.stylesheetText = text
    }

    override fun revert(app: EditorAppImpl) {
        app.state.stylesheet = oldSheet
        app.state.stylesheetText = oldText
    }
}