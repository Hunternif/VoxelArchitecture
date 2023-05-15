package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.setBlueprintNodeStyle
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.file.style.StyleParser

/**
 * Renders Blueprint node style in Style text editor.
 */
class GuiBlueprintNodeStyleAsText(
    private val app: EditorApp,
    private val node: BlueprintNode,
) {
    private val editor by lazy {
        GuiBlueprintNodeStyleTextEditor(app, node).apply {
            init()
        }
    }

    /** If history changes, we need to update items */
    private var lastAction: HistoryAction? = null

    fun render() {
        childWindow("editor_container", 250f, 300f) {
            editor.render()
        }
        checkForUpdates()
    }

    private fun checkForUpdates() {
        if (editor.isActive) return // don't update while editing
        val newLastAction = app.state.history.last()
        if (lastAction != newLastAction) {
            lastAction = newLastAction
            editor.loadText(node.rule.declarationsToString())
        }
    }
}

class GuiBlueprintNodeStyleTextEditor(
    app: EditorApp,
    private val node: BlueprintNode,
) : GuiStyleEditor(app, Mode.DECLARATIONS) {

    override fun applyStyleRules(parsed: StyleParser.StyleParseResult) {
        val rule = parsed.rules.firstOrNull() ?: return
        val oldStyleStr = node.rule.declarationsToString()
        val newStyleStr = rule.declarationsToString()
        app.setBlueprintNodeStyle(node, oldStyleStr, newStyleStr)
    }
}