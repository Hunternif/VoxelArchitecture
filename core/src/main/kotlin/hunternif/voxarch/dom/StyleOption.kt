package hunternif.voxarch.dom

import hunternif.voxarch.util.next
import kotlin.random.Random

/** For styling properties that take on a set of predefined values. */
typealias Option<T> = (seed: Long) -> T

fun <T> set(value: T): Option<T> = { _ -> value }

fun <T> random(vararg options: T): Option<T> {
    require(options.isNotEmpty()) { "options are empty" }
    return { seed -> Random(seed).next(*options) }
}