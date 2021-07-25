package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomLineSegmentBuilder
import hunternif.voxarch.plan.*
import kotlin.math.round

class StyleAlignment(
    internal val styled: StyledNode<Node>
) : StyleParameter

fun StyledNode<Node>.align(block: StyleAlignment.() -> Unit) {
    StyleAlignment(this).block()
}

//TODO: currently the current node's size must be set before alignment;
// Fix it so that styles can be set in any order and then sorted by priority.
/** Hugs the top of parent from outside. */
fun StyleAlignment.above() {
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y + p.height
            is Node -> origin.y = p.height
        }
    }
}

/** Hugs the top of parent from within. */
fun StyleAlignment.top() {
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y + p.height - this.height
            is Node -> origin.y = p.height - this.height
        }
    }
}

/** Hugs the bottom of parent from within. */
fun StyleAlignment.bottom() {
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y
            is Node -> origin.y = 0.0
        }
    }
}

/** Hugs the bottom of parent from outside. */
fun StyleAlignment.below() {
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y - this.height
            is Node -> origin.y = -this.height
        }
    }
}

/** Horizontal center of parent. */
fun StyleAlignment.center() {
    styled.node.run {
        val domParent = styled.domBuilder.parent
        val p = parent
        when {
            domParent is DomLineSegmentBuilder -> {
                origin.x = round(domParent.end.length()/2)
            }
            p is Room -> {
                origin.x = p.innerFloorCenter.x
                origin.z = p.innerFloorCenter.z
            }
            p is Node -> {
                origin.x = round(p.width/2)
                origin.z = round(p.length/2)
            }
        }
    }
}