package hunternif.voxarch.editor.actions.history

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons

abstract class HistoryAction(
    val description: String,
    val icon: String = FontAwesomeIcons.File,
) {
    abstract fun invoke(app: EditorAppImpl, firstTime: Boolean = false)
    abstract fun revert(app: EditorAppImpl)
    override fun toString(): String = description
}
