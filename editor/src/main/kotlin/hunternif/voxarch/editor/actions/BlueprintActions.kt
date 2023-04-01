package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.scenegraph.SceneNode


fun EditorApp.newBlueprint(): Blueprint {
    val action = NewBlueprint()
    historyAction(action)
    return action.bp
}

fun EditorApp.addNewBlueprint(node: SceneNode): Blueprint {
    val action = NewBlueprint(autoSelect = false)
    action { action.invoke(this) }
    addBlueprint(node, action.bp, true)
    return action.bp
}

fun EditorApp.addBlueprint(
    node: SceneNode, blueprint: Blueprint, autoSelect: Boolean = true
) = historyAction(AddBlueprint(node, blueprint, autoSelect))

/** Removes Blueprint from the node, but keeps it in the library. */
fun EditorApp.removeBlueprint(node: SceneNode, blueprint: Blueprint) =
    historyAction(RemoveBlueprint(node, blueprint))

/** Deletes Blueprint from the library */
fun EditorApp.deleteSelectedBlueprint() {
    val blueprint = state.selectedBlueprint ?: return
    historyAction(DeleteBlueprint(blueprint))
}

fun EditorApp.selectBlueprint(bp: Blueprint?) = historyAction(OpenBlueprint(bp))

/** Looks up the DomBuilder factory by name and creates a BP node with it.
 * If not found, returns null. */
fun EditorApp.newBlueprintNode(
    bp: Blueprint,
    name: String,
    x: Float = 0f,
    y: Float = 0f,
    autoLinkFrom: BlueprintSlot.Out? = null,
): BlueprintNode? {
    val factory = domBuilderFactoryByName[name] ?: return null
    val action = BlueprintNewNode(bp, name, factory(), x, y, autoLinkFrom)
    historyAction(action)
    return action.node
}

fun EditorApp.deleteBlueprintNode(node: BlueprintNode) =
    historyAction(BlueprintDeleteNode(node))

fun EditorApp.linkBlueprintSlots(from: BlueprintSlot.Out, to: BlueprintSlot.In) =
    historyAction(BlueprintCreateLink(from, to))

fun EditorApp.unlinkBlueprintLink(link: BlueprintLink) =
    historyAction(BlueprintUnlink(link.from, link.to))

fun EditorApp.deleteBlueprintParts(
    nodes: Collection<BlueprintNode>,
    links: Collection<BlueprintLink>,
) = historyAction(BlueprintDeleteParts(nodes, links))

fun EditorApp.setBlueprintNodeStyle(
    node: BlueprintNode,
    styleClass: String,
) = historyAction(BlueprintUpdateNode(node, styleClass = styleClass))

/** Only works for nodes with [DomRunBlueprint] */
fun EditorApp.setDelegateBlueprint(
    node: BlueprintNode,
    delegateBp: Blueprint
) = historyAction(BlueprintUpdateNode(node, delegateBp = delegateBp))