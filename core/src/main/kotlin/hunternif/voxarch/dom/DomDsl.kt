package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.generator.GenTurretDecor
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

/** Adds child [PolyRoom]. See [node]. */
fun DomBuilder.polyRoom(
    vararg styleClass: String,
    block: DomNodeBuilder<PolyRoom>.() -> Unit = {}
) {
    val bld = DomPolyRoomBuilder(ctx).apply { +styleClass }
    addChild(bld)
    bld.block()
}

/** Adds child [PolyRoom] with a [GenTurretDecor]. See [node]. */
fun DomBuilder.turret(
    vararg styleClass: String,
    block: DomPolyRoomBuilder.() -> Unit = {}
) {
    val bld = DomPolyRoomBuilder(ctx).apply {
        // The current node acts as the tower body, so we add style BLD_TOWER_BODY.
        addStyles(BLD_TOWER_BODY, DOM_TURRET, *styleClass)
    }
    addChild(bld)
    // must add generator after the polygon is added as child,
    // so that the generator inherits the stylesheet.
    bld.gen(GenTurretDecor(), *styleClass)
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

/** Runs [block] in every corner of this [PolyRoom]. */
fun DomBuilder.allCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicPolyCornerBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] in the 4 corners of this [Room]'s bounding box. */
fun DomBuilder.fourCorners(
    block: DomBuilder.() -> Unit = {}
) {
    val bld = DomLogicFourCornerBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] on every section of this polygon. */
fun DomBuilder.allWalls(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomPolySegmentBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] on one random section of this polygon. */
fun DomBuilder.randomWall(
    block: DomLineSegmentBuilder.() -> Unit = {}
) {
    val bld = DomRandomSegmentBuilder(ctx, block)
    addChild(bld)
}

/** Runs [block] on every side of this room. */
fun DomBuilder.fourWalls(
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
    val bld = DomBuilder(ctx)
    addChild(bld)
    bld.block()
}

/** Adds generator */
fun <G : IGenerator> DomBuilder.gen(
    generator: G,
    vararg styleClass: String,
    block: DomGenBuilder<G>.() -> Unit = {}
) {
    val bld = DomGenBuilder(ctx, generator).apply { +styleClass }
    addChild(bld)
    bld.block()
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