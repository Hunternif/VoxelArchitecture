package hunternif.voxarch.dom.style

/** Properties must be evaluated in this order. */
val GlobalStyleOrder : List<Property<*>> = listOf(
    PropSeed,

    PropHeight,
    PropWidth,
    PropLength,

    PropAlignY,
    PropAlignXZ,

    PropStartY,
    PropStartX,
    PropStartZ,

    PropY,
    PropX,
    PropZ,

    PropEdgeLength,
    PropShape,

    PropRoofShape,
//    PropBodyShape,
    PropBottomShape,
    PropRoofOffset,
    PropSpireRatio,
    PropTaperRatio,

    PropVisibility,
)

val GlobalStyleOrderIndex : Map<Property<*>, Int> by lazy {
    GlobalStyleOrder.withIndex().associate { it.value to it.index }
}
