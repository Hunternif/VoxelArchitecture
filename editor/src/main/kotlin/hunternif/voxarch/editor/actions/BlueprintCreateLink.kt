package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintCreateLink(
    private val from: BlueprintSlot.Out,
    private val to: BlueprintSlot.In,
) : HistoryAction(
    "Create blueprint link",
    FontAwesomeIcons.Link
) {
    override fun invoke(app: EditorAppImpl) {
        from.linkTo(to)
    }

    override fun revert(app: EditorAppImpl) {
        to.unlinkFrom(from)
    }
}