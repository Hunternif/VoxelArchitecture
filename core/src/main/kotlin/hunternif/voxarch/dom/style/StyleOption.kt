package hunternif.voxarch.dom.style

import hunternif.voxarch.util.next
import kotlin.random.Random

/** For styling properties that take on a set of predefined values. */
typealias Option<T> = (base: T, seed: Long) -> T

fun <T> set(value: T): Option<T> = { _, _ -> value }

fun <T> random(vararg options: T): Option<T> {
    require(options.isNotEmpty()) { "options are empty" }
    return { _, seed -> Random(seed).next(*options) }
}

/** Inherit the value from the parent node. */
fun <T> StyleParameter.inherit(): Option<T> = { base, _ -> base }