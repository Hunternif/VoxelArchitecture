package hunternif.voxarch.sandbox.castle.dsl

import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.util.clamp

@CastleDsl
class StyleSize(var min: Int = 0, var max: Int = Int.MAX_VALUE)

fun StyledNode.height(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node ?: return
    val style = StyleSize()
    val dim = style.block()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.height
        is Wall -> parent.height
        else -> 0.0
    }
    val newValue = dim(baseValue, seed + 10000001)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.height = newValue
        is Wall -> node.height = newValue
    }
}

fun StyledNode.width(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node ?: return
    val style = StyleSize()
    val dim = style.block()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.width
        else -> 0.0
    }
    val newValue = dim(baseValue, seed + 10000002)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.width = newValue
    }
}

fun StyledNode.length(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node ?: return
    val style = StyleSize()
    val dim = style.block()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.length
        else -> 0.0
    }
    val newValue = dim(baseValue, seed + 10000003)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.length = newValue
    }
}