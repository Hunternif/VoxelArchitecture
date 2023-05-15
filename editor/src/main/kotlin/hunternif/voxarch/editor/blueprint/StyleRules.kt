package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*

/**
 * Style properties available in Style editor.
 */
val styleEditorStyleProperties: List<Property<*>> by lazy {
    AllStyleProperties + PropBlueprint
}

/**
 * Style properties available in Blueprint editor.
 * Ordered as they would be in UI.
 */
val blueprintEditorStyleProperties: List<Property<*>> by lazy {
    listOf(
        PropWidth,
        PropHeight,
        PropDepth,

        PropSnapOrigin,

        PropAlignY,
        PropAlignXZ,
        PropAlignX,
        PropAlignZ,

        PropRotation,

        PropShape,

        PropRoofShape,
//        PropBodyShape,
        PropBottomShape,
        PropRoofOffset,
        PropSpireRatio,
        PropTaperRatio,

        PropEdgeLength,
        PropSideCount,

        PropPaddingTop,
        PropPaddingBottom,
        PropPaddingLeftX,
        PropPaddingRightX,
        PropPaddingBackZ,
        PropPaddingFrontZ,

        PropSubdivideDir,
        PropRepeatMode,
        PropRepeatGap,

        PropX,
        PropY,
        PropZ,
//        PropPosition,       // vector properties are not supported in UI

        PropOffsetY,
        PropOffsetX,
        PropOffsetZ,
//        PropOffsetPosition, // vector properties are not supported in UI

        PropStartX,
        PropStartY,
        PropStartZ,

        PropOffsetStartY,
        PropOffsetStartX,
        PropOffsetStartZ,

        PropSeed,
//        PropVisibility,
    )
}
