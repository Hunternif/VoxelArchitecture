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
fun StyleAlignment.above(margin: Dimension = 0.vx) {
    val marginValue = styled.run {
        margin(node.height, seed + 10000014)
    }
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y + p.height + marginValue
            is Node -> origin.y = p.height + marginValue
        }
    }
}

/** Hugs the top of parent from within. */
fun StyleAlignment.top(margin: Dimension = 0.vx) {
    val marginValue = styled.run {
        margin(node.height, seed + 10000015)
    }
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y + p.height - this.height - marginValue
            is Node -> origin.y = p.height - this.height - marginValue
        }
    }
}

/** Hugs the bottom of parent from within. */
fun StyleAlignment.bottom(margin: Dimension = 0.vx) {
    val marginValue = styled.run {
        margin(node.height, seed + 10000016)
    }
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y + marginValue
            is Node -> origin.y = marginValue
        }
    }
}

/** Hugs the bottom of parent from outside. */
fun StyleAlignment.below(margin: Dimension = 0.vx) {
    val marginValue = styled.run {
        margin(node.height, seed + 10000017)
    }
    styled.node.run {
        when (val p = parent) {
            is Room -> origin.y = p.start.y - this.height - marginValue
            is Node -> origin.y = -this.height - marginValue
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