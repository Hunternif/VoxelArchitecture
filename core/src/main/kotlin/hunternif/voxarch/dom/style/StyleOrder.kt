package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.style.property.*

/**
 * All usable style properties, in order in which they should be evaluated.
 *
 * Each group of properties has equal priority, so the properties within a group
 * are never reordered. They are evaluated in the same order they were added
 * to the Stylesheet.
 */
val GlobalStyleOrderByGroups: List<List<Property<*>>> by lazy {
    listOf(
        listOf(PropSeed),

        listOf(PropRotation),

        // Size is needed for alignment
        listOf(
            PropHeight,
            PropWidth,
            PropDepth,
            PropDiameter,
            PropSize,
        ),

        // Aspect ratio comes after size
        listOf(
            PropAspectRatioXZ,
            PropAspectRatioXY,
        ),

        // "snap origin" modifies both origin and start
        listOf(PropSnapOrigin),

        // Start defines where the origin sits, but it shouldn't affect alignment
        listOf(
            PropStartY,
            PropStartX,
            PropStartZ,
            PropStart,
        ),

        // Alignment snaps to a parent's side
        listOf(
            PropAlignY,
            PropAlignXZ,
            PropAlignX,
            PropAlignZ,
        ),

        // Padding can reduce size and move the node towards parent's center
        listOf(
            PropPaddingY,
            PropPaddingTop,
            PropPaddingBottom,
            PropPaddingX,
            PropPaddingLeftX,
            PropPaddingRightX,
            PropPaddingZ,
            PropPaddingBackZ,
            PropPaddingFrontZ,
        ),

        // Further changes to position will break alignment
        listOf(
            PropY,
            PropX,
            PropZ,
            PropPosition,
        ),

        // Position offset comes after all other position changes
        listOf(
            PropOffsetY,
            PropOffsetX,
            PropOffsetZ,
            PropOffsetPosition,
        ),
        listOf(
            PropOffsetStartY,
            PropOffsetStartX,
            PropOffsetStartZ,
            PropOffsetStart,
        ),

        // Cosmetic changes within the node
        listOf(PropEdgeLength),
        listOf(PropSideCount),
        listOf(PropShape),

        listOf(
            PropRoofShape,
//            PropBodyShape,
            PropBottomShape,
            PropRoofOffset,
            PropSpireRatio,
            PropTaperRatio,
        ),

        listOf(PropSubdivideDir),
        listOf(PropRepeatMode),
        listOf(PropRepeatGap),

        listOf(PropVisibility),

        // TODO: 'content' property should be executable multiple times
        listOf(PropContent),

        listOf(PropBuilder),
        listOf(PropClipMask),
    )
}

/**
 * List of all usable style properties.
 * For evaluation order, see [GlobalStyleOrderByGroups].
 */
val AllStyleProperties: List<Property<*>> by lazy {
    GlobalStyleOrderByGroups.flatten()
}

val GlobalStyleOrderIndex: Map<Property<*>, Int> by lazy {
    val map = mutableMapOf<Property<*>, Int>()
    GlobalStyleOrderByGroups.forEachIndexed { i, group ->
        group.forEach { map[it] = i }
    }
    map
}
