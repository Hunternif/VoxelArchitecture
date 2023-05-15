package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.file.style.StyleParser
import hunternif.voxarch.editor.gui.FontAwesomeIcons

/**
 * The input arguments are serialized to string, because that's easier
 * than implementing copy for all types of value.
 * [oldDeclStr] and [newDeclStr] contain lists of declarations, without selectors.
 */
class BlueprintUpdateNodeStyle(
    private val node: BlueprintNode,
    private val oldDeclStr: String,
    private val newDeclStr: String,
) : HistoryAction(
    "Change blueprint node style",
    FontAwesomeIcons.Code
) {
    private val parser = StyleParser()
    private lateinit var oldRule: Rule
    private lateinit var newRule: Rule

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldRule.isInitialized) {
            oldRule = parser.parseDeclarations(oldDeclStr).rules.firstOrNull() ?: Rule()
            newRule = parser.parseDeclarations(newDeclStr).rules.firstOrNull() ?: Rule()
        }
        node.rule.clear()
        node.rule.addAll(newRule.declarations)
    }

    override fun revert(app: EditorAppImpl) {
        node.rule.clear()
        node.rule.addAll(oldRule.declarations)
    }
}