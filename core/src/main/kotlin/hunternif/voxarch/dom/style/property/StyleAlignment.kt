package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.findLocalAABB
import hunternif.voxarch.plan.innerFloorCenter

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

    /** Vertical center of parent. */
    MIDDLE,

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

val PropAlignY = newNodeProperty<Node, AlignY>("align-y", AlignY.ORIGIN) { value ->
    val p = parentNode
    val nodeAABB = node.findLocalAABB()
    val baseValue = AlignY.ORIGIN
    val align = value.invoke(baseValue, seed)
    var newY = when (align) {
        AlignY.ABOVE -> p.height - nodeAABB.minY + 1
        AlignY.TOP -> p.height - nodeAABB.maxY
        AlignY.BOTTOM -> -nodeAABB.minY
        AlignY.BELOW -> -nodeAABB.maxY - 1
        AlignY.ORIGIN -> 0.0
        AlignY.MIDDLE -> p.height / 2 - node.height / 2 - nodeAABB.minY
    }
    if (align != AlignY.ORIGIN) newY += p.start.y
    node.origin.y = newY
}

val PropAlignX = newNodeProperty<Node, AlignX>("align-x", AlignX.ORIGIN) { value ->
    val p = parentNode
    val nodeAABB = node.findLocalAABB()
    val baseValue = AlignX.ORIGIN
    val align = value.invoke(baseValue, seed)
    val width = nodeAABB.maxX - nodeAABB.minX
    var newX = when (align) {
        AlignX.EAST_OUTSIDE -> p.width - nodeAABB.minX + 1
        AlignX.EAST_INSIDE -> p.width - nodeAABB.maxX
        AlignX.WEST_OUTSIDE -> -nodeAABB.maxX - 1
        AlignX.WEST_INSIDE -> -nodeAABB.minX
        AlignX.ORIGIN -> 0.0
        AlignX.CENTER -> p.width / 2 - width / 2 - nodeAABB.minX
    }
    if (align != AlignX.ORIGIN) newX += p.start.x
    node.origin.x = newX
}

val PropAlignZ = newNodeProperty<Node, AlignZ>("align-z", AlignZ.ORIGIN) { value ->
    val p = parentNode
    val nodeAABB = node.findLocalAABB()
    val baseValue = AlignZ.ORIGIN
    val align = value.invoke(baseValue, seed)
    val length = nodeAABB.maxZ - nodeAABB.minZ
    var newZ = when (align) {
        AlignZ.SOUTH_OUTSIDE -> p.depth - nodeAABB.minZ + 1
        AlignZ.SOUTH_INSIDE -> p.depth - nodeAABB.maxZ
        AlignZ.NORTH_OUTSIDE -> -nodeAABB.maxZ - 1
        AlignZ.NORTH_INSIDE -> -nodeAABB.minZ
        AlignZ.ORIGIN -> 0.0
        AlignZ.CENTER -> p.depth / 2 - length / 2 - nodeAABB.minZ
    }
    if (align != AlignZ.ORIGIN) newZ += p.start.z
    node.origin.z = newZ
}

val PropAlignXZ = newNodeProperty<Node, AlignXZ>("align-xz", AlignXZ.ORIGIN) { value ->
    val baseValue = AlignXZ.ORIGIN
    val align = value.invoke(baseValue, seed)
    if (align == AlignXZ.CENTER) {
        PropAlignX.applyTo(this, set(AlignX.CENTER))
        PropAlignZ.applyTo(this, set(AlignZ.CENTER))
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

/** Vertical center of parent. */
fun StyleAlignmentY.middle() = set(AlignY.MIDDLE)


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