package hunternif.voxarch.dom.style

import hunternif.voxarch.util.next
import kotlin.random.Random

/**
 * At runtime this will be invoked, and the result will be applied to the
 * styled property of a Node/DomBuilder.
 */
interface Value<T> {
    operator fun invoke(base: T, seed: Long): T
    /** True if the value is a percentage from the parent Node's value. */
    val isPct: Boolean
}


fun <T> set(text: String, value: T): Value<T> = value(text) { _, _ -> value }
fun <T> set(value: T): Value<T> = set(value.toString(), value)

fun <T> random(vararg options: T): Value<T> {
    require(options.isNotEmpty()) { "options are empty" }
    return value("random") { _, seed -> Random(seed).next(*options) }
}

/** Inherit the value from the parent node. */
fun <T> inherit(): Value<T> = value("inherit", true) { base, _ -> base }

fun <T> value(
    strValue: String = "...",
    isPct: Boolean = false,
    method: (base: T, seed: Long) -> T,
) = object : Value<T> {
    override fun invoke(base: T, seed: Long): T = method(base, seed)
    override val isPct: Boolean = isPct
    override fun toString(): String = strValue
}