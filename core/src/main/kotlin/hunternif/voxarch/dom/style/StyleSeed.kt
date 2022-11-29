package hunternif.voxarch.dom.style

class StyleSeed : StyleParameter

typealias Seed = Value<Long>

val PropSeed = newDomProperty<Seed> { value ->
    val baseValue = domBuilder.parent.seed
    val newValue = value.invoke(baseValue, seed + 10000018)
    seed = newValue
}

/**
 * Using `inherit()` on the seed will make this element use the same seed
 * as the parent, but its own children will use their own seeds.
 */
fun Rule.seed2(block: StyleSeed.() -> Seed) {
    add(PropSeed, StyleSeed().block())
}
