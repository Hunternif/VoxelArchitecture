package hunternif.voxarch.sandbox.castle.dsl

import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.util.clamp

interface Dimension {
    fun calculate(base: Double): Double
}

internal class StaticDimension(private val value: Double) : Dimension {
    override fun calculate(base: Double): Double = value
}

internal class PercentDimension(private val percent: Double) : Dimension {
    override fun calculate(base: Double): Double = base * 0.01 * percent
}

val Int.vx: Dimension get() = StaticDimension(this.toDouble())
val Int.pct: Dimension get() = PercentDimension(this.toDouble())

@CastleDsl
class StyleSize(var min: Int = 0, var max: Int = Int.MAX_VALUE)

fun StyledNode.height(block: StyleSize.() -> Dimension) {
    val style = StyleSize()
    val dim = style.block()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.height
        is Wall -> parent.height
        else -> 0.0
    }
    val newValue = dim.calculate(baseValue)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.height = newValue
        is Wall -> node.height = newValue
    }
}

fun StyledNode.width(block: StyleSize.() -> Dimension) {
    val style = StyleSize()
    val dim = style.block()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.width
        else -> 0.0
    }
    val newValue = dim.calculate(baseValue)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.width = newValue
    }
}

fun StyledNode.length(block: StyleSize.() -> Dimension) {
    val style = StyleSize()
    val dim = style.block()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.length
        else -> 0.0
    }
    val newValue = dim.calculate(baseValue)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.length = newValue
    }
}