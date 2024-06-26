package hunternif.voxarch.dom

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D

// Some DOM elements are built of more basic classes,
// e.g. by adding child Nodes to a Room. To help identify this initial parent
// Node, a style will be automatically added to it.
const val DOM_TURRET = "dom_turret"


/** Creates DOM root. */
fun domRoot(
    block: DomRoot.() -> Unit = {}
): DomRoot {
    return DomRoot().apply { block() }
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
    this.addChildNodeBuilder(*styleClass) { Node() }.block()
}

/** Adds child [Room]. See [node]. */
fun DomBuilder.room(
    vararg styleClass: String,
    block: DomNodeBuilder<Room>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Room() }.block()
}

/** Adds child [Floor]. See [node]. */
fun DomBuilder.floor(
    vararg styleClass: String,
    block: DomNodeBuilder<Floor>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Floor() }.block()
}

/** Adds child [Staircase]. See [node]. */
fun DomBuilder.stairs(
    vararg styleClass: String,
    block: DomNodeBuilder<Staircase>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Staircase() }.block()
}

/** Adds child [PolyRoom]. See [node]. */
fun DomBuilder.polyRoom(
    vararg styleClass: String,
    block: DomPolyRoomBuilder<PolyRoom>.() -> Unit = {}
) {
    val bld = DomPolyRoomBuilder().apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child [Node] that takes 100% space. See [node]. */
fun DomBuilder.space(
    vararg styleClass: String,
    block: DomNodeBuilder<Node>.() -> Unit = {}
) {
    this.addChildNodeBuilder(BLD_SPACE, *styleClass) { Node() }.block()
}

/** Adds child [PolyRoom] with a [DomTurretDecor]. See [node]. */
fun DomBuilder.turret(
    vararg styleClass: String,
    block: DomPolyRoomWithTurretBuilder.() -> Unit = {}
) {
    val bld = DomPolyRoomWithTurretBuilder().apply {
        addStyles(*styleClass)
    }
    addChild(bld)
    bld.block()
}

/** Adds decoration to make the parent node look like a turret. */
fun DomBuilder.turretDecor(
    vararg styleClass: String,
    block: DomTurretDecor.() -> Unit = {}
) {
    val bld = DomTurretDecor().apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Runs [block] in every corner of this [PolyRoom]. */
fun DomBuilder.allCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicPolyCornerBuilder()
    addChild(bld)
    bld.block()
}

/** Runs [block] in the 4 corners of this [Room]'s bounding box. */
fun DomBuilder.fourCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicFourCornerBuilder()
    addChild(bld)
    bld.block()
}

/** Runs [block] on every section of this polygon. */
fun DomBuilder.allWalls(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomPolySegmentBuilder()
    addChild(bld)
    bld.block()
}

/** Runs [block] on one random section of this polygon. */
fun DomBuilder.randomWall(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomRandomSegmentBuilder()
    addChild(bld)
    bld.block()
}

/** Runs [block] on every side of this room. */
fun DomBuilder.fourWalls(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomFourWallsBuilder()
    addChild(bld)
    bld.block()
}

/** Creates a [Wall] on the line segment. */
fun DomBuilder.wall(
    vararg styleClass: String,
    block: DomNodeBuilder<Wall>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Wall() }.block()
}

/** Adds a wall with a window type assigned to it. */
fun DomBuilder.archedWindow(
    vararg styleClass: String,
    block: DomNodeBuilder<Window>.() -> Unit = {}
) {
    this.addChildNodeBuilder(BLD_ARCHED_WINDOW, *styleClass) { Window() }.block()
}

/** Creates a [Path] on the line segment. */
fun DomBuilder.path(
    vararg styleClass: String,
    block: DomNodeBuilder<Path>.() -> Unit = {}
) {
    val bld = DomPathBuilder().apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child [Prop]. See [node]. */
fun DomBuilder.prop(
    propType: String,
    vararg styleClass: String,
    block: DomNodeBuilder<Prop>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Prop(propType) }.block()
}

/** Adds child [Column]. See [node]. */
fun DomBuilder.column(
    vararg styleClass: String,
    block: DomNodeBuilder<Column>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Column() }.block()
}

/** Adds child [Slope]. See [node]. */
fun DomBuilder.slope(
    vararg styleClass: String,
    block: DomNodeBuilder<Slope>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { Slope() }.block()
}

/** Adds child [SlopedRoof]. See [node]. */
fun DomBuilder.slopedRoof(
    vararg styleClass: String,
    block: DomNodeBuilder<SlopedRoof>.() -> Unit = {}
) {
    this.addChildNodeBuilder(*styleClass) { SlopedRoof() }.block()
}

/** Adds an extra element in the DOM that doesn't do anything.
 * This is useful for wrapping other DOM builders. */
fun DomBuilder.passthrough(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomBuilder()
    addChild(bld)
    bld.block()
}

/** Extend room in any horizontal direction: North, South, East, West. */
fun DomBuilder.extend(
    block: DomExtend.() -> Unit = {}
) {
    val bld = DomExtend()
    addChild(bld)
    bld.block()
}

/** Subdivides space and lays out children along the Y axis up. */
fun DomBuilder.subdivide(
    dir: Direction3D,
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomSubdivide(dir)
    addChild(bld)
    bld.block()
}

/** Executes only 1 child at random. */
fun DomBuilder.random(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomRandom()
    addChild(bld)
    bld.block()
}

///////////////////////////// Utility /////////////////////////////
@DslMarker
annotation class DomDsl

/** Creates a child [DomBuilder], adds it to parent and returns. */
private inline fun <reified N : Node> DomBuilder.addChildNodeBuilder(
    vararg styleClass: String,
    noinline createNode: () -> N
): DomNodeBuilder<N> {
    val child = DomNodeBuilder(createNode).apply { +styleClass }
    addChild(child)
    return child
}