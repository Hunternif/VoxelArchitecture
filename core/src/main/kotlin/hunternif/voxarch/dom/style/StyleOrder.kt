package hunternif.voxarch.dom.style

/** Properties must be evaluated in this order. */
val GlobalStyleOrder : List<Property<*>> = listOf(
    PropSeed,

    PropHeight,
    PropWidth,
    PropLength,

    // Alignment goes here

    PropStartY,
    PropStartX,
    PropStartZ,

    PropY,
    PropX,
    PropZ,
)

val GlobalStyleOrderIndex : Map<Property<*>, Int> by lazy {
    GlobalStyleOrder.withIndex().associate { it.value to it.index }
}
