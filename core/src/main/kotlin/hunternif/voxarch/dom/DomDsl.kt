package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.BLD_TOWER_BODY
import hunternif.voxarch.vector.Vec3

// Some DOM elements are built of more basic classes via generators,
// e.g. by adding child Nodes to a Room. To help identify this initial parent
// Node, a style will be automatically added to it.
const val DOM_TURRET = "dom_turret"


/**
 * Adds child empty [Node].
 *
 * Styles from this DOM's [Stylesheet] will be queried by names and applied
 * to the [Node] in the given order.
 *
 * @param styleClass names of style classes (like in CSS).
 * The first class name will be used as the node type.
 */
fun DomBuilder<Node?>.node(
    vararg styleClass: String,
    block: DomBuilder<Node>.() -> Unit = {}
) {
    this.createChild(styleClass) { Node() }.block()
}

/** Adds child [Room]. See [node]. */
fun DomBuilder<Node?>.room(
    vararg styleClass: String,
    block: DomBuilder<Room>.() -> Unit = {}
) {
    this.createChild(styleClass) { Room() }.block()
}

/** Adds child [Floor]. See [node]. */
fun DomBuilder<Node?>.floor(
    vararg styleClass: String,
    block: DomBuilder<Floor>.() -> Unit = {}
) {
    this.createChild(styleClass) { Floor() }.block()
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

/** Adds child PolygonRoom with a [TurretGenerator]. See [node]. */
fun DomBuilder<Node?>.turret(
    vararg styleClass: String,
    block: DomPolygonRoomBuilder.() -> Unit = {}
) {
    val bld =  DomPolygonRoomBuilder().apply{
        // The current node acts as the tower body, so we add style BLD_TOWER_BODY.
        // BLD_TOWER_BODY must go first, so that it's also used as node type.
        addStyles(BLD_TOWER_BODY, DOM_TURRET, *styleClass)
    }
    addChild(bld)
    bld.generators.add(TurretGenerator())
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
    val bld = DomLogicPolygonCornerBuilder(block)
    addChild(bld)
}

/** Runs [block] in the 4 corners of this [Room]'s bounding box. */
fun DomBuilder<Room>.fourCorners(
    block: DomBuilder<Node?>.() -> Unit = {}
) {
    val bld = DomLogicFourCornerBuilder(block)
    addChild(bld)
}

/** Runs [block] on every section of this polygon. */
fun DomBuilder<Room>.allWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomPolygonSegmentBuilder(block)
    addChild(bld)
}

/** Runs [block] on one random section of this polygon. */
fun DomBuilder<Room>.randomWall(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomRandomSegmentBuilder(block)
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
    this.createChild(styleClass) { Wall(Vec3.ZERO, end) }.block()
}

/** Creates a [Path] on the line segment. */
fun DomLineSegmentBuilder.path(
    vararg styleClass: String,
    block: DomBuilder<Path>.() -> Unit = {}
) {
    this.createChild(styleClass) { Path(Vec3.ZERO, Vec3.ZERO, end) }.block()
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