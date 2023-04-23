package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.BlueprintEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintCreateLink(
    private val from: BlueprintSlot.Out,
    private val to: BlueprintSlot.In,
) : HistoryAction(
    "Create blueprint link",
    FontAwesomeIcons.Link
), BlueprintEvent {
    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        from.linkTo(to)
    }

    override fun revert(app: EditorAppImpl) {
        to.unlinkFrom(from)
    }
}