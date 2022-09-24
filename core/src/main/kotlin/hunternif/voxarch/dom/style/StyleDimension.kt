package hunternif.voxarch.dom.style

import hunternif.voxarch.util.round
import kotlin.random.Random

/** For styling [Double]-typed properties */
interface Dimension : Option<Double>

/** voxels (blocks) */
val Int.vx: Dimension get() = dimension { _, _ -> this.toDouble() }
/** voxels (blocks) */
val Double.vx: Dimension get() = dimension { _, _ -> this }

/** percent vs parent size */
val Int.pct: Dimension get() = dimension { base, _ -> base * 0.01 * this.toDouble() }
/** percent vs parent size */
val Double.pct: Dimension get() = dimension { base, _ -> base * 0.01 * this }

/** Inherit the value from the parent node. */
fun StyleSize.inherit(): Dimension = dimension { base, _ -> base }

/** random value between given min and max, based on seed */
infix fun Dimension.to(upperBound: Dimension): Dimension = dimension { base, seed ->
    val fromVal = this(base, seed)
    val toVal = upperBound(base, seed) + 0.0000001
    Random(seed).nextDouble(fromVal, toVal).round()
}

operator fun Dimension.plus(other: Dimension): Dimension {
    return dimension {base, seed -> this(base, seed) + other(base, seed)}
}

operator fun Dimension.minus(other: Dimension): Dimension {
    return dimension {base, seed -> this(base, seed) - other(base, seed)}
}

operator fun Dimension.unaryMinus(): Dimension {
    return dimension {base, seed -> -this(base, seed)}
}

operator fun Dimension.times(value: Double): Dimension {
    return dimension {base, seed -> this(base, seed) * value }
}

operator fun Dimension.times(value: Int): Dimension {
    return dimension {base, seed -> this(base, seed) * value }
}

operator fun Double.times(value: Dimension): Dimension {
    return dimension {base, seed -> this * value(base, seed) }
}

operator fun Int.times(value: Dimension): Dimension {
    return dimension {base, seed -> this * value(base, seed) }
}

operator fun Dimension.div(value: Double): Dimension {
    return dimension {base, seed -> this(base, seed) / value }
}

operator fun Dimension.div(value: Int): Dimension {
    return dimension {base, seed -> this(base, seed) / value }
}

fun Dimension.clamp(min: Dimension, max: Dimension): Dimension {
    return dimension { base, seed ->
        val minVal = min(base, seed)
        val maxVal = max(base, seed)
        val thisVal = this(base, seed)
        when {
            thisVal < minVal -> minVal
            thisVal > maxVal -> maxVal
            else -> thisVal
        }
    }
}

private fun dimension(
    method: (base: Double, seed: Long) -> Double,
) = object : Dimension {
    override fun invoke(base: Double, seed: Long): Double = method(base, seed)
}