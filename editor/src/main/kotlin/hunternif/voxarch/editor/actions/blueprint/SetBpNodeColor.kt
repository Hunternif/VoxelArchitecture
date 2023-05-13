package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.util.ColorRGBa

class SetBpNodeColor(
    val node: BlueprintNode,
    var oldColor: ColorRGBa,
    var newColor: ColorRGBa,
) : HistoryAction(
    "Change blueprint node color",
    FontAwesomeIcons.Code
) {
    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        node.color.set(newColor)
    }

    override fun revert(app: EditorAppImpl) {
        node.color.set(oldColor)
    }
}