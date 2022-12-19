package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomLineSegmentBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import kotlin.math.round

class StyleAlignmentY : StyleParameter
class StyleAlignmentX : StyleParameter
class StyleAlignmentZ : StyleParameter
class StyleAlignmentXZ : StyleParameter

enum class AlignY {
    /** Parent's origin */
    ORIGIN,

    /** Hugs the top of parent from outside. */
    ABOVE,

    /** Hugs the top of parent from within. */
    TOP,

    /** Hugs the bottom of parent from outside. */
    BELOW,

    /** Hugs the bottom of parent from within. */
    BOTTOM,
}

enum class AlignXZ {
    /** Parent's origin */
    ORIGIN,

    /** Horizontal center of parent. */
    CENTER,
}

enum class AlignX {
    /** Parent's origin */
    ORIGIN,

    /** Horizontal center of parent. */
    CENTER,

    /** Hugs parent's positive X side from within. */
    EAST_INSIDE,

    /** Hugs parent's positive X side from outside. */
    EAST_OUTSIDE,

    /** Hugs parent's negative X side from within. */
    WEST_INSIDE,

    /** Hugs parent's negative X side from outside. */
    WEST_OUTSIDE,
}

enum class AlignZ {
    /** Parent's origin */
    ORIGIN,

    /** Horizontal center of parent. */
    CENTER,

    /** Hugs parent's positive Z side from within. */
    SOUTH_INSIDE,

    /** Hugs parent's positive Z side from outside. */
    SOUTH_OUTSIDE,

    /** Hugs parent's negative Z side from within. */
    NORTH_INSIDE,

    /** Hugs parent's negative Z side from outside. */
    NORTH_OUTSIDE,
}

val PropAlignY = newNodeProperty<Node, AlignY>("align y", AlignY.ORIGIN) { value ->
    val p = parentNode
    val baseValue = AlignY.ORIGIN
    val align = value.invoke(baseValue, seed)
    var newY = when (align) {
        AlignY.ABOVE -> p.height
        AlignY.TOP -> p.height - node.height
        AlignY.BOTTOM -> 0.0
        AlignY.BELOW -> -node.height
        AlignY.ORIGIN -> 0.0
    }
    if (p is Room && align != AlignY.ORIGIN) newY += p.start.y
    node.origin.y = newY
}

val PropAlignX = newNodeProperty<Node, AlignX>("align x", AlignX.ORIGIN) { value ->
    val p = parentNode
    val baseValue = AlignX.ORIGIN
    val align = value.invoke(baseValue, seed)
    var newX = when (align) {
        AlignX.EAST_OUTSIDE -> p.length
        AlignX.EAST_INSIDE -> p.length - node.length
        AlignX.WEST_OUTSIDE -> -node.length
        AlignX.WEST_INSIDE -> 0.0
        AlignX.ORIGIN -> 0.0
        AlignX.CENTER -> p.length / 2 - node.length / 2
    }
    if (p is Room && align != AlignX.ORIGIN) newX += p.start.x
    node.origin.x = newX
    if (node is Room) {
        // We assumed the room had start at (0, 0, 0). Fix that:
        node.origin.x -= node.start.x
    }
}

val PropAlignZ = newNodeProperty<Node, AlignZ>("align z", AlignZ.ORIGIN) { value ->
    val p = parentNode
    val baseValue = AlignZ.ORIGIN
    val align = value.invoke(baseValue, seed)
    var newZ = when (align) {
        AlignZ.SOUTH_OUTSIDE -> p.width
        AlignZ.SOUTH_INSIDE -> p.width - node.width
        AlignZ.NORTH_OUTSIDE -> -node.width
        AlignZ.NORTH_INSIDE -> 0.0
        AlignZ.ORIGIN -> 0.0
        AlignZ.CENTER -> p.width / 2 - node.width / 2
    }
    if (p is Room && align != AlignZ.ORIGIN) newZ += p.start.z
    node.origin.z = newZ
    if (node is Room) {
        // We assumed the room had start at (0, 0, 0). Fix that:
        node.origin.z -= node.start.z
    }
}

val PropAlignXZ = newNodeProperty<Node, AlignXZ>("align xz", AlignXZ.ORIGIN) { value ->
    val p = parentNode
    val domParent = ctx.parent
    val baseValue = AlignXZ.ORIGIN
    val align = value.invoke(baseValue, seed)
    if (align == AlignXZ.CENTER) {
        when {
            domParent is DomLineSegmentBuilder -> {
                node.origin.x = round(domParent.end.length() / 2)
            }
            p is Room -> {
                node.origin.x = p.innerFloorCenter.x
                node.origin.z = p.innerFloorCenter.z
            }
            else -> {
                node.origin.x = round(p.length / 2)
                node.origin.z = round(p.width / 2)
            }
        }
    }
    if (node is Room) {
        // We assumed the Node was 0 size, and centered it as a single point.
        // Now account for start:
        node.origin.x -= node.innerFloorCenter.x
        node.origin.z -= node.innerFloorCenter.z
    }
}

fun Rule.alignXZ(block: StyleAlignmentXZ.() -> Value<AlignXZ>) {
    add(PropAlignXZ, StyleAlignmentXZ().block())
}

fun Rule.alignY(block: StyleAlignmentY.() -> Value<AlignY>) {
    add(PropAlignY, StyleAlignmentY().block())
}

fun Rule.alignX(block: StyleAlignmentX.() -> Value<AlignX>) {
    add(PropAlignX, StyleAlignmentX().block())
}

fun Rule.alignZ(block: StyleAlignmentZ.() -> Value<AlignZ>) {
    add(PropAlignZ, StyleAlignmentZ().block())
}


// Y

/** Hugs the top of parent from outside. */
fun StyleAlignmentY.above() = set(AlignY.ABOVE)

/** Hugs the top of parent from within. */
fun StyleAlignmentY.top() = set(AlignY.TOP)

/** Hugs the bottom of parent from outside. */
fun StyleAlignmentY.below() = set(AlignY.BELOW)

/** Hugs the bottom of parent from within. */
fun StyleAlignmentY.bottom() = set(AlignY.BOTTOM)


// XZ

/** Horizontal center of parent. */
fun StyleAlignmentXZ.center() = set(AlignXZ.CENTER)


// X

/** Horizontal center of parent. */
fun StyleAlignmentX.center() = set(AlignX.CENTER)

/** Hugs parent's positive X side from within. */
fun StyleAlignmentX.eastIn() = set(AlignX.EAST_INSIDE)

/** Hugs parent's positive X side from outside. */
fun StyleAlignmentX.eastOut() = set(AlignX.EAST_OUTSIDE)

/** Hugs parent's negative X side from within. */
fun StyleAlignmentX.westIn() = set(AlignX.WEST_INSIDE)

/** Hugs parent's negative X side from outside. */
fun StyleAlignmentX.westOut() = set(AlignX.WEST_OUTSIDE)


// Z

/** Horizontal center of parent. */
fun StyleAlignmentZ.center() = set(AlignZ.CENTER)

/** Hugs parent's positive Z side from within. */
fun StyleAlignmentZ.southIn() = set(AlignZ.SOUTH_INSIDE)

/** Hugs parent's positive Z side from outside. */
fun StyleAlignmentZ.southOut() = set(AlignZ.SOUTH_OUTSIDE)

/** Hugs parent's negative Z side from within. */
fun StyleAlignmentZ.northIn() = set(AlignZ.NORTH_INSIDE)

/** Hugs parent's negative Z side from outside. */
fun StyleAlignmentZ.northOut() = set(AlignZ.NORTH_OUTSIDE)