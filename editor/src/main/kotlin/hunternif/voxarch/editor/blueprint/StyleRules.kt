package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*

/** Ordered as they would be in UI. */
val editorStyleProperties: List<Property<*>> = listOf(
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
//    PropBodyShape,
    PropBottomShape,
    PropRoofOffset,
    PropSpireRatio,
    PropTaperRatio,

    PropEdgeLength,

    PropPaddingTop,
    PropPaddingBottom,
    PropPaddingLeftX,
    PropPaddingRightX,
    PropPaddingBackZ,
    PropPaddingFrontZ,

    PropX,
    PropY,
    PropZ,
    PropPosition,

    PropOffsetY,
    PropOffsetX,
    PropOffsetZ,
    PropOffsetPosition,

    PropStartX,
    PropStartY,
    PropStartZ,

    PropOffsetStartY,
    PropOffsetStartX,
    PropOffsetStartZ,

    PropSeed,
//    PropVisibility,
)
