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
    private val oldStyle: Stylesheet,
    private val oldText: String,
    private var newStyle: Stylesheet,
    private var newText: String,
) : HistoryAction("Update stylesheet", FontAwesomeIcons.FileCode) {

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        app.state.stylesheet = newStyle
        app.state.stylesheetText = newText
        app.reloadStyleEditor()
    }

    override fun revert(app: EditorAppImpl) {
        app.state.stylesheet = oldStyle
        app.state.stylesheetText = oldText
        app.reloadStyleEditor()
    }

    /** Combine this old action with the next consecutive action. */
    fun update(nextAction: SetStylesheet) {
        this.newStyle = nextAction.newStyle
        this.newText = nextAction.newText
    }
}