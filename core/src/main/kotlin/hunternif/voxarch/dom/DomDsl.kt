package hunternif.voxarch.dom

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

/**
 * Adds child empty [Node].
 *
 * Styles from this DOM's [Stylesheet] will be queried by names and applied
 * to the [Node] in the given order.
 *
 * @param styleClass names of style classes (like in CSS).
 */
fun DomBuilder.node(
    vararg styleClass: String,
    block: DomBuilder.() -> Unit = {}
) {
    createChild(styleClass) { Node() }.block()
}

/** Adds child [Room]. See [node]. */
fun DomBuilder.room(
    vararg styleClass: String,
    block: DomBuilder.() -> Unit = {}
) {
    createChild(styleClass) { Room() }.block()
}
