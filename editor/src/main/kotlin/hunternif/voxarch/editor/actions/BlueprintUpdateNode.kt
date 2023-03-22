package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintUpdateNode(
    private val node: BlueprintNode,
    private val styleClass: String,
) : HistoryAction(
    "Update blueprint node",
    FontAwesomeIcons.Code
) {
    lateinit var oldStyleClass: String

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldStyleClass.isInitialized) {
            oldStyleClass = node.extraStyleClass
        }
        node.extraStyleClass = styleClass
    }

    override fun revert(app: EditorAppImpl) {
        node.extraStyleClass = oldStyleClass
    }
}