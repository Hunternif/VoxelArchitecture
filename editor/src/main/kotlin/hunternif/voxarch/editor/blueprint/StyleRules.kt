package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.style.*

/** Ordered as they would be in UI. */
val editorStyleProperties: List<Property<*>> = listOf(
    PropHeight,
    PropWidth,
    PropLength,

    PropAlignY,
    PropAlignXZ,

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

    PropStartX,
    PropStartY,
    PropStartZ,

    PropSeed,
//    PropVisibility,
)
