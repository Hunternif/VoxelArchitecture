package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
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
    val blueprint: Blueprint get() = bpCopy

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (firstTime) {
            val xml = serializeToXmlStr(originalBp)
            bpCopy = deserializeXml(xml, Blueprint::class)

            // Delegate nodes can't be deserialized immediately, and need
            // special attention:
            // - to reference the delegated BP;
            // - to connect the delegate's out slots to nodes.
            val delegateNodes = originalBp.findNodesByType<DomRunBlueprint>()
            delegateNodes.forEach { origNode ->
                val domBuilder = origNode.domBuilder as DomRunBlueprint
                val copyNode = bpCopy.findNodeById(origNode.id)!!
                val copyDomBuilder = copyNode.domBuilder as DomRunBlueprint
                copyDomBuilder.blueprint = domBuilder.blueprint

                // populate out slots:
                origNode.outputs.forEach { outSlot ->
                    val copySlot = origNode.outputs.first { it.name == outSlot.name }
                    outSlot.links.forEach { link ->
                        val copyToNode = bpCopy.findNodeById(link.to.node.id)!!
                        copySlot.linkTo(copyToNode.inputs.first())
                    }
                }
            }
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