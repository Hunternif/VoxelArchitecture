package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.BLD_TOWER_BODY
import hunternif.voxarch.vector.Vec3

// Some DOM elements are built of more basic classes,
// e.g. by adding child Nodes to a Room. To help identify this initial parent
// Node, a style will be automatically added to it.
const val DOM_TURRET = "dom_turret"


/** Creates DOM root. */
fun domRoot(
    node: Node = Structure(),
    block: DomRoot.() -> Unit = {}
): DomRoot {
    return DomRoot(node).apply {
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

/** Adds child [PolyRoom]. See [node]. */
fun DomBuilder.polyRoom(
    vararg styleClass: String,
    block: DomNodeBuilder<PolyRoom>.() -> Unit = {}
) {
    val bld = DomPolyRoomBuilder().apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child [PolyRoom] with a [DomTurretDecor]. See [node]. */
fun DomBuilder.turret(
    vararg styleClass: String,
    block: DomPolyRoomWithTurretBuilder.() -> Unit = {}
) {
    val bld = DomPolyRoomWithTurretBuilder().apply {
        // The current node acts as the tower body, so we add style BLD_TOWER_BODY.
        addStyles(BLD_TOWER_BODY, DOM_TURRET, *styleClass)
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

/** Adds child [Ward] with a perimeter of walls and towers. */
fun DomBuilder.ward(
    vararg styleClass: String,
    block: DomWardBuilder.() -> Unit = {}
) {
    val bld = DomWardBuilder().apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Runs [block] in every corner of this [PolyRoom]. */
fun DomBuilder.allCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicPolyCornerBuilder(block)
    addChild(bld)
}

/** Runs [block] in the 4 corners of this [Room]'s bounding box. */
fun DomBuilder.fourCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicFourCornerBuilder(block)
    addChild(bld)
}

/** Runs [block] on every section of this polygon. */
fun DomBuilder.allWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomPolySegmentBuilder(block)
    addChild(bld)
}

/** Runs [block] on one random section of this polygon. */
fun DomBuilder.randomWall(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomRandomSegmentBuilder(block)
    addChild(bld)
}

/** Runs [block] on every side of this room. */
fun DomBuilder.fourWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomFourWallsBuilder(block)
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

/** Adds child [Prop]. See [node]. */
fun DomBuilder.prop(
    propType: String,
    vararg styleClass: String,
    block: DomNodeBuilder<Prop>.() -> Unit = {}
) {
    this.addChildNodeBuilder(styleClass) { Prop(propType) }.block()
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

///////////////////////////// Utility /////////////////////////////
@DslMarker
annotation class DomDsl

/** Creates a child [DomBuilder], adds it to parent and returns. */
private inline fun <reified N : Node> DomBuilder.addChildNodeBuilder(
    styleClass: Array<out String>,
    noinline createNode: () -> N
): DomNodeBuilder<N> {
    val child = DomNodeBuilder(createNode).apply { +styleClass }
    addChild(child)
    return child
}