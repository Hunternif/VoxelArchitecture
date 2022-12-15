package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomLineSegmentBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.*
import kotlin.math.round

class StyleAlignment : StyleParameter

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
                node.origin.x = round(p.width / 2)
                node.origin.z = round(p.length / 2)
            }
        }
    }
}

fun Rule.alignXZ(block: StyleAlignment.() -> Value<AlignXZ>) {
    add(PropAlignXZ, StyleAlignment().block())
}

fun Rule.alignY(block: StyleAlignment.() -> Value<AlignY>) {
    add(PropAlignY, StyleAlignment().block())
}

/** Hugs the top of parent from outside. */
fun StyleAlignment.above() = set(AlignY.ABOVE)

/** Hugs the top of parent from within. */
fun StyleAlignment.top() = set(AlignY.TOP)

/** Hugs the bottom of parent from outside. */
fun StyleAlignment.below() = set(AlignY.BELOW)

/** Hugs the bottom of parent from within. */
fun StyleAlignment.bottom() = set(AlignY.BOTTOM)

/** Horizontal center of parent. */
fun StyleAlignment.center() = set(AlignXZ.CENTER)