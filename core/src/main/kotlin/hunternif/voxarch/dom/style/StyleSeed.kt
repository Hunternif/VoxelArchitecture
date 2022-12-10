package hunternif.voxarch.dom.style

class StyleSeed : StyleParameter

val PropSeed = newDomProperty<Long>("seed", 0) { value ->
    val baseValue = domBuilder.parent.seed
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
