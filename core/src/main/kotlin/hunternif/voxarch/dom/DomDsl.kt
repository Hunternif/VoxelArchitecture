package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.*
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
    createChild(styleClass) { Node() }.block()
}

/** Adds child [Room]. See [node]. */
fun DomBuilder<Node?>.room(
    vararg styleClass: String,
    block: DomBuilder<Room>.() -> Unit = {}
) {
    createChild(styleClass) { Room() }.block()
}

/** Adds child [Floor]. See [node]. */
fun DomBuilder<Node?>.floor(
    vararg styleClass: String,
    block: DomBuilder<Floor>.() -> Unit = {}
) {
    createChild(styleClass) { Floor() }.block()
}

/** Adds child [PolygonRoom]. See [node]. */
fun DomBuilder<Node?>.polygonRoom(
    vararg styleClass: String,
    block: DomBuilder<PolygonRoom>.() -> Unit = {}
) {
    val bld = DomPolygonRoomBuilder().apply{ +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child [Turret]. See [node]. */
fun DomBuilder<Node?>.turret(
    vararg styleClass: String,
    block: DomTurretBuilder.() -> Unit = {}
) {
    val bld =  DomTurretBuilder().apply{ +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child [Ward] with a perimeter of walls and towers. */
fun DomBuilder<Node?>.ward(
    vararg styleClass: String,
    block: DomWardBuilder.() -> Unit = {}
) {
    val bld = DomWardBuilder().apply{ +styleClass }
    addChild(bld)
    bld.block()
}

/** Runs [block] in every corner of this [PolygonRoom]. */
fun DomBuilder<PolygonRoom>.allCorners(
    block: DomBuilder<Node?>.() -> Unit = {}
) {
    val bld = DomLogicCornerBuilder(block)
    addChild(bld)
}

/** Runs [block] on every section of this node. */
fun DomBuilder<PolygonRoom>.allWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomPolygonSegmentBuilder(block)
    addChild(bld)
}

/** Runs [block] on every side of this room. */
fun DomBuilder<Room>.fourWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomFourWallsBuilder(block)
    addChild(bld)
}

/** Creates a [Wall] on the line segment. */
fun DomLineSegmentBuilder.wall(
    vararg styleClass: String,
    block: DomBuilder<Wall>.() -> Unit = {}
) {
    createChild(styleClass) { Wall(p1, p2) }.block()
}

///////////////////////////// Utility /////////////////////////////
@DslMarker
annotation class CastleDsl

/** Creates a child [DomBuilder], adds it to parent and returns. */
private fun <N: Node> DomBuilder<Node?>.createChild(
    styleClass: Array<out String>,
    createNode: () -> N
) : DomBuilder<N> {
    val child = DomNodeBuilder(createNode).apply{ +styleClass }
    addChild(child)
    return child
}