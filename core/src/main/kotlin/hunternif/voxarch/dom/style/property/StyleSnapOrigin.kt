package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.OriginSnap
import hunternif.voxarch.util.OriginSnap.*
import hunternif.voxarch.util.snapStart

class StyleSnapOrigin : StyleParameter

val PropSnapOrigin = newNodeProperty<Node, OriginSnap>("snap origin", OFF) { value ->
    val newValue = value.invoke(OFF, seed + 10000027)
    // Don't move origin, because this can mess up offsets added by other styles
    node.snapStart(newValue)
}

/**
 * Set a standard places where the node's origin will be
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