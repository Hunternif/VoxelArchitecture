package hunternif.voxarch.editor.actions.history

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.AppAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons

abstract class HistoryAction(
    val description: String,
    val icon: String = FontAwesomeIcons.File,
) : AppAction {
    override fun invoke(app: EditorAppImpl) {
        invoke(app, false)
    }

    abstract fun invoke(app: EditorAppImpl, firstTime: Boolean = false)
    abstract fun revert(app: EditorAppImpl)
    override fun toString(): String = description
}
