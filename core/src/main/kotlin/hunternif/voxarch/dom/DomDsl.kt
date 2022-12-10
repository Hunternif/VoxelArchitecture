package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.BLD_TOWER_BODY
import hunternif.voxarch.vector.Vec3

// Some DOM elements are built of more basic classes via generators,
// e.g. by adding child Nodes to a Room. To help identify this initial parent
// Node, a style will be automatically added to it.
const val DOM_TURRET = "dom_turret"


/** Creates DOM root. */
fun domRoot(
    stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0L,
    node: Node = Structure(),
    block: DomRoot.() -> Unit = {}
): DomRoot {
    return DomContext(stylesheet, seed, node).rootBuilder.apply {
        block()
    }
}

/**
 * Adds child empty [Node].
 *
 * Styles from this DOM's [Stylesheet] will be queried by names and applied
 * to the [Node] in the given order.
 *
 * @param styleClass names of style classes (like in CSS).
 * Class names are also added to node tags.
 */
fun DomBuilder.node(
    vararg styleClass: String,
    block: DomNodeBuilder<Node>.() -> Unit = {}
) {
    this.addChildNodeBuilder(styleClass) { Node() }.block()
}

/** Adds child [Room]. See [node]. */
fun DomBuilder.room(
    vararg styleClass: String,
    block: DomNodeBuilder<Room>.() -> Unit = {}
) {
    this.addChildNodeBuilder(styleClass) { Room() }.block()
}

/** Adds child [Floor]. See [node]. */
fun DomBuilder.floor(
    vararg styleClass: String,
    block: DomNodeBuilder<Floor>.() -> Unit = {}
) {
    this.addChildNodeBuilder(styleClass) { Floor() }.block()
}

/** Adds child [PolygonRoom]. See [node]. */
fun DomBuilder.polygonRoom(
    vararg styleClass: String,
    block: DomNodeBuilder<PolygonRoom>.() -> Unit = {}
) {
    val bld = DomPolygonRoomBuilder(ctx).apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child PolygonRoom with a [TurretGenerator]. See [node]. */
fun DomBuilder.turret(
    vararg styleClass: String,
    block: DomPolygonRoomBuilder.() -> Unit = {}
) {
    val bld = DomPolygonRoomBuilder(ctx).apply {
        // The current node acts as the tower body, so we add style BLD_TOWER_BODY.
        addStyles(BLD_TOWER_BODY, DOM_TURRET, *styleClass)
    }
    addChild(bld)
    bld.generators.add(TurretGenerator())
    bld.block()
}

/** Adds child [Ward] with a perimeter of walls and towers. */
fun DomBuilder.ward(
    vararg styleClass: String,
    block: DomWardBuilder.() -> Unit = {}
) {
    val bld = DomWardBuilder(ctx).apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Runs [block] in every corner of this [PolygonRoom]. */
fun DomNodeBuilder<out PolygonRoom>.allCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicPolygonCornerBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] in the 4 corners of this [Room]'s bounding box. */
fun DomNodeBuilder<out Room>.fourCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicFourCornerBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] on every section of this polygon. */
fun DomNodeBuilder<out Room>.allWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomPolygonSegmentBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] on one random section of this polygon. */
fun DomNodeBuilder<out Room>.randomWall(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomRandomSegmentBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] on every side of this room. */
fun DomNodeBuilder<out Room>.fourWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomFourWallsBuilder(ctx, block)
    addChild(bld)
}

/** Creates a [Wall] on the line segment. */
fun DomLineSegmentBuilder.wall(
    vararg styleClass: String,
    block: DomNodeBuilder<Wall>.() -> Unit = {}
) {
    this.addChildNodeBuilder(styleClass) { Wall(Vec3.ZERO, end) }.block()
}

/** Creates a [Path] on the line segment. */
fun DomLineSegmentBuilder.path(
    vararg styleClass: String,
    block: DomNodeBuilder<Path>.() -> Unit = {}
) {
    this.addChildNodeBuilder(styleClass) { Path(Vec3.ZERO, Vec3.ZERO, end) }.block()
}

///////////////////////////// Utility /////////////////////////////
@DslMarker
annotation class CastleDsl

/** Creates a child [DomBuilder], adds it to parent and returns. */
private inline fun <reified N : Node> DomBuilder.addChildNodeBuilder(
    styleClass: Array<out String>,
    noinline createNode: () -> N
): DomNodeBuilder<N> {
    val child = DomNodeBuilder(ctx, createNode).apply { +styleClass }
    addChild(child)
    return child
}