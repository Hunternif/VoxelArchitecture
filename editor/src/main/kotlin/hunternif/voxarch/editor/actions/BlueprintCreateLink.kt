package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
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
    private lateinit var link: BlueprintLink

    override fun invoke(app: EditorAppImpl) {
        link = from.linkTo(to)
    }

    override fun revert(app: EditorAppImpl) {
        link.unlink()
    }
}