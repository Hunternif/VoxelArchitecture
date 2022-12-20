package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*

/** Ordered as they would be in UI. */
val editorStyleProperties: List<Property<*>> = listOf(
    PropHeight,
    PropLength,
    PropWidth,

    PropAlignY,
    PropAlignXZ,

    PropRotation,

    PropShape,

    PropRoofShape,
//    PropBodyShape,
    PropBottomShape,
    PropRoofOffset,
    PropSpireRatio,
    PropTaperRatio,

    PropEdgeLength,

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

    PropSeed,
//    PropVisibility,
)
