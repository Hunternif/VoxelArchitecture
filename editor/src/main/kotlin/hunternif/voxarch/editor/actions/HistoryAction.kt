package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons

abstract class HistoryAction(
    val description: String,
    val icon: String = FontAwesomeIcons.File,
) {
    abstract fun invoke(app: EditorAppImpl, firstTime: Boolean)
    abstract fun revert(app: EditorAppImpl)
    override fun toString(): String = description
}
