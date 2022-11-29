package hunternif.voxarch.dom.style

import hunternif.voxarch.util.next
import kotlin.random.Random

fun <T> set(value: T): Value<T> = value { _, _ -> value }

fun <T> random(vararg options: T): Value<T> {
    require(options.isNotEmpty()) { "options are empty" }
    return value { _, seed -> Random(seed).next(*options) }
}

/** Inherit the value from the parent node. */
fun <T> StyleParameter.inherit(): Value<T> = value { base, _ -> base }

fun <T> value(
    method: (base: T, seed: Long) -> T,
) = object : Value<T> {
    override fun invoke(base: T, seed: Long): T = method(base, seed)
}