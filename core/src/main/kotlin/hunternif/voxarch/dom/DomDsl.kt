package hunternif.voxarch.dom

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room

/**
 * Adds child empty [Node].
 *
 * Styles from this DOM's [Stylesheet] will be queried by names and applied
 * to the [Node] in the given order.
 *
 * @param styleClass names of style classes (like in CSS).
 */
fun DomBuilder<Node?>.node(
    vararg styleClass: String,
    block: DomBuilder<Node>.() -> Unit = {}
) {
    createChild<Node>(styleClass) { Node() }.block()
}

/** Adds child [Room]. See [node]. */
fun DomBuilder<Node?>.room(
    vararg styleClass: String,
    block: DomBuilder<Room>.() -> Unit = {}
) {
    createChild<Room>(styleClass) { Room() }.block()
}

/** Adds child [Floor]. See [node]. */
fun DomBuilder<Node?>.floor(
    vararg styleClass: String,
    block: DomBuilder<Floor>.() -> Unit = {}
) {
    createChild<Floor>(styleClass) { Floor() }.block()
}

/** Adds child [Ward] with a perimeter of walls and towers. */
fun DomBuilder<Node?>.ward(
    vararg styleClass: String,
    block: DomWardBuilder.() -> Unit = {}
) {
    val bld = DomWardBuilder(styleClass.toList(), this, nextChildSeed())
    children.add(bld)
    bld.block()
}

/** Runs [block] in every corner of this [Ward]. */
fun DomWardBuilder.allCorners(
    block: DomBuilder<Node?>.() -> Unit = {}
) {
    allCornerBuild = block
}