package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.style.property.*

/**
 * List of all usable style properties,
 * in order how they are evaluated.
 */
val GlobalStyleOrder : List<Property<*>> = listOf(
    PropSeed,

    PropRotation,

    // Size is needed for alignment
    PropHeight,
    PropWidth,
    PropDepth,

    // "snap origin" modifies both origin and start
    PropSnapOrigin,

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

    // Padding can reduce size and move the node towards parent's center
    PropPaddingTop,
    PropPaddingBottom,
    PropPaddingLeftX,
    PropPaddingRightX,
    PropPaddingBackZ,
    PropPaddingFrontZ,

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
    PropOffsetStartY,
    PropOffsetStartX,
    PropOffsetStartZ,
    PropOffsetStart,

    // Cosmetic changes within the node
    PropEdgeLength,
    PropShape,

    PropRoofShape,
//    PropBodyShape,
    PropBottomShape,
    PropRoofOffset,
    PropSpireRatio,
    PropTaperRatio,

    PropSubdivideDir,

    PropVisibility,
)

val GlobalStyleOrderIndex : Map<Property<*>, Int> by lazy {
    GlobalStyleOrder.withIndex().associate { it.value to it.index }
}
