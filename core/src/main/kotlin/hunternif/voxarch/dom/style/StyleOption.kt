package hunternif.voxarch.dom.style

import hunternif.voxarch.util.next
import kotlin.random.Random

/**
 * At runtime this will be invoked, and the result will be applied to the
 * styled property of a Node/Generator.
 */
interface Option<T> {
    operator fun invoke(base: T, seed: Long): T

}

fun <T> set(value: T): Option<T> = option { _, _ -> value }

fun <T> random(vararg options: T): Option<T> {
    require(options.isNotEmpty()) { "options are empty" }
    return option { _, seed -> Random(seed).next(*options) }
}

/** Inherit the value from the parent node. */
fun <T> StyleParameter.inherit(): Option<T> = option { base, _ -> base }

fun <T> option(
    method: (base: T, seed: Long) -> T,
) = object : Option<T> {
    override fun invoke(base: T, seed: Long): T = method(base, seed)
}