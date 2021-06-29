package hunternif.voxarch.sandbox.castle.dsl

import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.util.clamp
import hunternif.voxarch.util.round
import kotlin.random.Random

interface Dimension {
    fun calculate(base: Double, seed: Long): Double
}

internal class StaticDimension(private val value: Double) : Dimension {
    override fun calculate(base: Double, seed: Long): Double = value
}
internal class PercentDimension(private val percent: Double) : Dimension {
    override fun calculate(base: Double, seed: Long): Double = base * 0.01 * percent
}
internal class RandomDimension(
    private val from: Dimension,
    private val to: Dimension
) : Dimension {
    override fun calculate(base: Double, seed: Long): Double {
        val fromVal = from.calculate(base, seed)
        val toVal = to.calculate(base, seed)
        return Random(seed).nextDouble(fromVal, toVal).round()
    }
}

/** voxels (blocks) */
val Int.vx: Dimension get() = StaticDimension(this.toDouble())
/** percent vs parent size */
val Int.pct: Dimension get() = PercentDimension(this.toDouble())
/** random value between given min and max, based on seed */
infix fun Dimension.to(max: Dimension): Dimension = RandomDimension(this, max)

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
    val newValue = dim.calculate(baseValue, seed + 10000001)
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
    val newValue = dim.calculate(baseValue, seed + 10000002)
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
    val newValue = dim.calculate(baseValue, seed + 10000003)
        .clamp(style.min, style.max)
    when (node) {
        is Room -> node.length = newValue
    }
}