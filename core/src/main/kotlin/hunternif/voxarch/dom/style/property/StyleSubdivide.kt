package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomRepeat
import hunternif.voxarch.dom.builder.IDirectionBuilder
import hunternif.voxarch.dom.builder.RepeatMode
import hunternif.voxarch.dom.builder.RepeatMode.*
import hunternif.voxarch.dom.style.newDomProperty
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*

val PropSubdivideDir = newDomProperty<DomBuilder, Direction3D>("subdivide-dir", EAST) { value ->
    val newValue = value.invoke(EAST, seed + 10000034)
    (domBuilder as? IDirectionBuilder)?.dir = newValue
}

val PropRepeatMode = newDomProperty<DomRepeat, RepeatMode>("repeat-mode", OFF) { value ->
    val newValue = value.invoke(OFF, seed + 10000035)
    domBuilder.mode = newValue
}

// TODO: even if you remove this declaration, the changed value remains on
//  the DomBuilder. Need to apply it to a temporary object, e.g. StyledElement.
val PropRepeatGap = newDomProperty<DomRepeat, Double>("repeat-gap", 0.0) { value ->
    val baseValue = domBuilder.run { parentNode.dirSize }
    val newValue = value.invoke(baseValue, seed + 10000036)
    domBuilder.gap = newValue
}