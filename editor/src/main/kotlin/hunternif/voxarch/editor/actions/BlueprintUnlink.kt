package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintUnlink(
    private val from: BlueprintSlot.Out,
    private val to: BlueprintSlot.In,
) : HistoryAction(
    "Remove blueprint link",
    FontAwesomeIcons.Unlink
) {
    override fun invoke(app: EditorAppImpl) {
        to.unlinkFrom(from)
    }

    override fun revert(app: EditorAppImpl) {
        from.linkTo(to)
    }
}