package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.file.deserializeXml
import hunternif.voxarch.editor.file.serializeToXmlStr
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class CopyBlueprint(
    private val originalBp: Blueprint,
    private val autoSelect: Boolean = true,
) : HistoryAction("Copy blueprint", FontAwesomeIcons.Copy) {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    private lateinit var bpCopy: Blueprint

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (firstTime) {
            val xml = serializeToXmlStr(originalBp)
            bpCopy = deserializeXml(xml, Blueprint::class)
        }
        oldSelected = app.state.selectedBlueprint
        newSelected = if (autoSelect) bpCopy else oldSelected
        app.state.blueprintRegistry.save(bpCopy)
        if (autoSelect) OpenBlueprint(newSelected).invoke(app)
    }

    override fun revert(app: EditorAppImpl) {
        app.state.blueprintRegistry.remove(bpCopy)
        OpenBlueprint(oldSelected).invoke(app)
    }
}