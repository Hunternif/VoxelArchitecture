package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import imgui.extension.imnodes.ImNodes

class BlueprintDeleteParts(
    private val nodes: Collection<BlueprintNode>,
    private val links: Collection<BlueprintLink>,
) : HistoryAction(
    makeDescription(nodes, links),
    FontAwesomeIcons.TrashAlt
) {
    companion object {
        private fun makeDescription(
            nodes: Collection<BlueprintNode>,
            links: Collection<BlueprintLink>,
        ) = "Delete blueprint " + when {
            nodes.isEmpty() && links.isNotEmpty() -> "links"
            nodes.isNotEmpty() && links.isEmpty() -> "nodes"
            else -> "parts"
        }
    }

    /** Includes initial [links] and links from deleted nodes */
    private lateinit var allLinks: List<BlueprintLink>

    override fun invoke(app: EditorAppImpl) {
        if (!::allLinks.isInitialized) {
            allLinks = links +
                nodes.flatMap { it.inputs.flatMap { it.links } } +
                nodes.flatMap { it.outputs.flatMap { it.links } }
        }
        nodes.forEach { it.bp.removeNode(it) }
        links.forEach { it.unlink() }
    }

    override fun revert(app: EditorAppImpl) {
        nodes.forEach {
            it.bp.nodes.add(it)
            ImNodes.setNodeEditorSpacePos(it.id, it.x, it.y)
        }
        allLinks.forEach { it.from.linkTo(it.to) }
    }
}