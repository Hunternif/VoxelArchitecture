package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomSubdivide
import hunternif.voxarch.dom.style.newDomProperty
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*

val PropSubdivideDir = newDomProperty<DomSubdivide, Direction3D>("subdivide-dir", EAST) { value ->
    val newValue = value.invoke(EAST, seed + 10000034)
    domBuilder.dir = newValue
}