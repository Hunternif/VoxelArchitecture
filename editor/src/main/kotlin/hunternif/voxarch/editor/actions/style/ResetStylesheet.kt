package hunternif.voxarch.editor.actions.style

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.StyleEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons

/**
 * Sets initial stylesheet text, based on a template.
 */
class ResetStylesheet : HistoryAction(
    "Reset stylesheet",
    FontAwesomeIcons.History
), StyleEvent {
    private lateinit var oldText: String
    private lateinit var oldStylesheet: Stylesheet

    private lateinit var newText: String
    private lateinit var newStylesheet: Stylesheet

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::newText.isInitialized) {
            oldText = app.state.stylesheetText
            oldStylesheet = app.state.stylesheet
            newStylesheet = defaultStyle
            newText = formatDefaultStylesheet(newStylesheet)
        }
        app.state.stylesheet = newStylesheet
        app.state.stylesheetText = newText
    }

    override fun revert(app: EditorAppImpl) {
        app.state.stylesheet = oldStylesheet
        app.state.stylesheetText = oldText
    }
}

fun formatDefaultStylesheet(stylesheet: Stylesheet): String =
"""// Defaults

$stylesheet


// Custom rules
"""