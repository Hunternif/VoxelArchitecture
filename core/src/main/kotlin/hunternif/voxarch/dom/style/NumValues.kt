package hunternif.voxarch.dom.style

import hunternif.voxarch.util.round
import kotlin.random.Random

typealias NumVal = Value<Number>

/** voxels (blocks) */
val Int.vx: NumVal get() = number(toString()) { _, _ -> this }

/** voxels (blocks) */
val Float.vx: NumVal get() = number(toString()) { _, _ -> this.toDouble() }

/** voxels (blocks) */
val Double.vx: NumVal get() = number(toString()) { _, _ -> this }

/** percent vs parent size */
val Int.pct: NumVal
    get() = number("$this%", true) { base, _ ->
        base.toDouble() * 0.01 * this.toDouble()
    }

/** percent vs parent size */
val Float.pct: NumVal
    get() = number("$this%", true) { base, _ ->
        base.toDouble() * 0.01 * this.toDouble()
    }

/** percent vs parent size */
val Double.pct: NumVal
    get() = number("$this%", true) { base, _ ->
        base.toDouble() * 0.01 * this
    }

/** random value between given min and max, based on seed */
infix fun NumVal.to(upperBound: NumVal): NumVal =
    number("($this ~ $upperBound)", isPctOr(upperBound))
    { base, seed ->
        val fromVal = this(base, seed).toDouble()
        val toVal = upperBound(base, seed).toDouble() + 0.0000001
        Random(seed).nextDouble(fromVal, toVal).round()
    }

operator fun NumVal.plus(other: NumVal): NumVal {
    return number("($this + $other)", isPctOr(other))
    { base, seed -> this(base, seed).toDouble() + other(base, seed).toDouble() }
}


operator fun NumVal.minus(other: NumVal): NumVal {
    return number("($this - $other)", isPctOr(other))
    { base, seed -> this(base, seed).toDouble() - other(base, seed).toDouble() }
}

operator fun NumVal.unaryMinus(): NumVal {
    return number("(-$this)", isPct)
    { base, seed -> -this(base, seed).toDouble() }
}

operator fun NumVal.times(value: Number): NumVal {
    return number("$this * $value", isPct)
    { base, seed -> this(base, seed).toDouble() * value.toDouble() }
}

operator fun Number.times(value: NumVal): NumVal {
    return number("$this * $value", value.isPct)
    { base, seed -> this.toDouble() * value(base, seed).toDouble() }
}

operator fun NumVal.div(value: Number): NumVal {
    return number("$this / $value", isPct)
    { base, seed -> this(base, seed).toDouble() / value.toDouble() }
}

fun NumVal.clamp(min: NumVal, max: NumVal): NumVal {
    return number("clamp($min, $max)", isPctOr(min, max))
    { base, seed ->
        val minVal = min(base, seed).toDouble()
        val maxVal = max(base, seed).toDouble()
        val thisVal = this(base, seed).toDouble()
        when {
            thisVal < minVal -> minVal
            thisVal > maxVal -> maxVal
            else -> thisVal
        }
    }
}

fun min(a: NumVal, b: NumVal): NumVal {
    return number("min($a, $b)", a.isPct || b.isPct)
    { base, seed ->
        kotlin.math.min(a(base, seed).toDouble(), b(base, seed).toDouble())
    }
}

fun max(a: NumVal, b: NumVal): NumVal {
    return number("max($a, $b)", a.isPct || b.isPct)
    { base, seed ->
        kotlin.math.max(a(base, seed).toDouble(), b(base, seed).toDouble())
    }
}

fun <N: Number> number(
    strValue: String = "number",
    isPct: Boolean = false,
    method: (base: N, seed: Long) -> N,
) = object : Value<N> {
    override fun invoke(base: N, seed: Long): N = method(base, seed)
    override val isPct: Boolean = isPct
    override fun toString(): String = strValue
}

fun Value<Number>.toDouble(): Value<Double> = number(toString(), isPct) {
    base, seed -> invoke(base, seed).toDouble()
}

fun Value<Number>.toInt(): Value<Int> = number(toString(), isPct) {
    base, seed -> invoke(base, seed).toInt()
}

/** Shorthand to check if this or [other] is a percentage-based value. */
@PublishedApi
internal fun Value<out Number>.isPctOr(
    other: Value<out Number>,
) = isPct || other.isPct

@PublishedApi
internal fun Value<out Number>.isPctOr(
    other: Value<out Number>,
    another: Value<out Number>,
) = isPct || other.isPct || another.isPct