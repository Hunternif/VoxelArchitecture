package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintCreateLink(
    private val from: BlueprintSlot.Out,
    private val to: BlueprintSlot.In,
) : HistoryAction(
    "Create blueprint link",
    FontAwesomeIcons.Link
) {
    private var oldLinkFrom: BlueprintLink? = null
    private var oldLinkTo: BlueprintLink? = null

    override fun invoke(app: EditorAppImpl) {
        oldLinkFrom = from.link
        oldLinkTo = to.link
        from.linkTo(to)
    }

    override fun revert(app: EditorAppImpl) {
        to.unlink()
        oldLinkFrom?.run { from.linkTo(to) }
        oldLinkTo?.run { from.linkTo(to) }
    }
}