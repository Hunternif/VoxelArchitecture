package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.IRoofDomBuilder
import hunternif.voxarch.dom.style.newDomProperty
import hunternif.voxarch.plan.naturalWidth
import kotlin.math.roundToInt

/**
 * How far a roof's end is away from the wall.
 * This applies to turret spires and gable roofs.
 */
val PropRoofOffset = newDomProperty<DomBuilder, Double>("roof-offset", 1.0) { value ->
    val baseValue = parentNode.naturalWidth
    (domBuilder as IRoofDomBuilder).roofOffset = value
        .invoke(baseValue, seed + 10000006)
        .roundToInt()
}