package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.*

class StyleSeed : StyleParameter

//TODO: create seed offset property?

val PropSeed = newDomProperty<DomBuilder, Long>("seed", 0) { value ->
    val baseValue = ctx.parent?.seed ?: 0L
    val newValue = value.invoke(baseValue, seed + 10000025)
    seed = newValue
}

/**
 * Using `inherit()` on the seed will make this element use the same seed
 * as the parent, but its own children will use their own seeds.
 */
fun Rule.seed(block: StyleSeed.() -> Value<Long>) {
    add(PropSeed, StyleSeed().block())
}
