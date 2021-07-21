package hunternif.voxarch.dom

import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.turret.Turret

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

/** Adds child [PolygonRoom]. See [node]. */
fun DomBuilder<Node?>.polygonRoom(
    vararg styleClass: String,
    block: DomBuilder<PolygonRoom>.() -> Unit = {}
) {
    val bld =  DomPolygonRoomBuilder(
        styleClass.toList(),
        this,
        nextChildSeed()
    )
    children.add(bld)
    bld.block()
}

/** Creates walls along the polygon. See [node]. */
fun DomBuilder<PolygonRoom>.walls(
    vararg styleClass: String,
    block: DomBuilder<Wall>.() -> Unit = {}
) {
    //TODO: wall DOM builder
}

/** Adds child [Turret]. See [node]. */
fun DomBuilder<Node?>.turret(
    vararg styleClass: String,
    block: DomTurretBuilder.() -> Unit = {}
) {
    val bld =  DomTurretBuilder(styleClass.toList(), this, nextChildSeed())
    children.add(bld)
    bld.block()
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