package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.style.property.*

/** Properties must be evaluated in this order. */
val GlobalStyleOrder : List<Property<*>> = listOf(
    PropSeed,

    PropRotation,

    // Size is needed for alignment
    PropHeight,
    PropLength,
    PropWidth,

    // Start defines where the origin sits, but it shouldn't affect alignment
    PropStartY,
    PropStartX,
    PropStartZ,
    PropStart,

    // Alignment snaps to a parent's side
    PropAlignY,
    PropAlignXZ,
    PropAlignX,
    PropAlignZ,

    // Further changes to position will break alignment
    PropY,
    PropX,
    PropZ,
    PropPosition,

    // Position offset comes after all other position changes
    PropOffsetY,
    PropOffsetX,
    PropOffsetZ,
    PropOffsetPosition,

    // Cosmetic changes within the node
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
