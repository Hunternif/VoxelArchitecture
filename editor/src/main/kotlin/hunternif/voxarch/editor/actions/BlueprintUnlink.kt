package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintUnlink(private val link: BlueprintLink) : HistoryAction(
    "Remove blueprint link",
    FontAwesomeIcons.Unlink
) {
    override fun invoke(app: EditorAppImpl) {
        link.unlink()
    }

    override fun revert(app: EditorAppImpl) {
        link.from.linkTo(link.to)
    }
}