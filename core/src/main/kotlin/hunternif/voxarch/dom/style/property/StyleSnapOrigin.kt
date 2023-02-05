package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.OriginSnap
import hunternif.voxarch.util.OriginSnap.*
import hunternif.voxarch.util.snapOrigin

class StyleSnapOrigin : StyleParameter

val PropSnapOrigin = newNodeProperty<Node, OriginSnap>("snap origin", OFF) { value ->
    val newValue = value.invoke(OFF, seed + 10000027)
    node.snapOrigin(newValue)
}

/**
 * Standard places where a node's origin could automatically snap to,
 * relative to its bounding box.
 */
fun Rule.snapOrigin(block: StyleSnapOrigin.() -> Value<OriginSnap>) {
    add(PropSnapOrigin, StyleSnapOrigin().block())
}

/** Not snapping */
fun StyleSnapOrigin.off() = set(OFF)

/** Low-XYZ corner */
fun StyleSnapOrigin.corner() = set(CORNER)

/** Center of the floor */
fun StyleSnapOrigin.floorCenter() = set(FLOOR_CENTER)

/** Center of the box, on all axes. */
fun StyleSnapOrigin.center() = set(CENTER)