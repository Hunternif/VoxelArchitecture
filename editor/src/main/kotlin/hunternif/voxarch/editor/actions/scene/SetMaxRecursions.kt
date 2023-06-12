package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.util.clamp

class SetMaxRecursions(newMax: Int) : HistoryAction(
    "Set max recursions",
    FontAwesomeIcons.DiceThree,
) {
    private val newMax: Int = newMax.clamp(min, max)
    private var oldMax: Int = 0

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (firstTime) {
            oldMax = app.state.maxRecursions
        }
        app.state.maxRecursions = newMax
    }

    override fun revert(app: EditorAppImpl) {
        app.state.maxRecursions = oldMax
    }

    companion object {
        const val min = 0
        const val max = 99
    }
}