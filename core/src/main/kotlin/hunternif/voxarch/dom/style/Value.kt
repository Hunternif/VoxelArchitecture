package hunternif.voxarch.dom.style

import hunternif.voxarch.util.next
import kotlin.random.Random

/**
 * At runtime this will be invoked, and the result will be applied to the
 * styled property of a Node/DomBuilder.
 */
interface Value<T> {
    operator fun invoke(base: T, seed: Long): T
}


fun <T> set(value: T): Value<T> = value(value.toString()) { _, _ -> value }

fun <T> random(vararg options: T): Value<T> {
    require(options.isNotEmpty()) { "options are empty" }
    return value("random") { _, seed -> Random(seed).next(*options) }
}

/** Inherit the value from the parent node. */
fun <T> StyleParameter.inherit(): Value<T> = value("inherit") { base, _ -> base }

fun <T> value(
    strValue: String = "...",
    method: (base: T, seed: Long) -> T,
) = object : Value<T> {
    override fun invoke(base: T, seed: Long): T = method(base, seed)
    override fun toString(): String = strValue
}