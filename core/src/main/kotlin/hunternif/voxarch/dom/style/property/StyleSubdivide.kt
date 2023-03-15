package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.IDirectionBuilder
import hunternif.voxarch.dom.style.newDomProperty
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*

val PropSubdivideDir = newDomProperty<DomBuilder, Direction3D>("subdivide-dir", EAST) { value ->
    val newValue = value.invoke(EAST, seed + 10000034)
    (domBuilder as? IDirectionBuilder)?.dir = newValue
}