package hunternif.voxarch.dom

import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall

@CastleDsl
class StyleSize(
    var min: Dimension = 0.vx,
    var max: Dimension = Int.MAX_VALUE.vx
)

fun StyledNode.height(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node ?: return
    val style = StyleSize()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.height
        is Wall -> parent.height
        else -> 0.0
    }
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000001)
    when (node) {
        is Room -> node.height = newValue
        is Wall -> node.height = newValue
    }
}

fun StyledNode.width(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node ?: return
    val style = StyleSize()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.width
        else -> 0.0
    }
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000002)
    when (node) {
        is Room -> node.width = newValue
    }
}

fun StyledNode.length(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node ?: return
    val style = StyleSize()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.length
        is Wall -> parent.length
        else -> 0.0
    }
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000003)
    when (node) {
        is Room -> node.length = newValue
    }
}