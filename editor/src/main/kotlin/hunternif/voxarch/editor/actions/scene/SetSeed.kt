package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class SetSeed(private val newSeed: Long) : HistoryAction(
    "Set seed",
    FontAwesomeIcons.DiceThree,
) {
    private var oldSeed: Long = 0

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (firstTime) {
            oldSeed = app.state.seed
        }
        app.state.seed = newSeed
    }

    override fun revert(app: EditorAppImpl) {
        app.state.seed = oldSeed
    }
}