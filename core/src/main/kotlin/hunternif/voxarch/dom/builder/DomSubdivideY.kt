package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.dom.style.pct
import hunternif.voxarch.dom.style.property.PropHeight
import hunternif.voxarch.plan.Node
import kotlin.math.min
import kotlin.math.round

/**
 * Subdivides space and lays out children along the Y direction.
 * Rounds size to integer.
 */
class DomSubdivideY : DomBuilder() {
    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        if (children.isEmpty()) return children

        val allNodes = ArrayList<Node>(children.size)
        val solidNodes = ArrayList<Node>(children.size)
        val pctNodes = ArrayList<Node>(children.size)
        children.forEach {
            if (it is StyledNode<*>) {
                allNodes.add(it.node)
                val props = it.ctx.stylesheet.getProperties(it)
                val height = props[PropHeight]?.value ?: 100.pct
                if (height.isPct) pctNodes.add(it.node)
                else solidNodes.add(it.node)
            }
        }

        val parentNode = children.first().parentNode
        // + 1 to get "natural" size
        val total = parentNode.height + 1

        // Get "natural" sizes pre-layout
        val solidTotal = solidNodes.fold(0.0) { o, node -> o + node.height + 1}
        val pctTotal = pctNodes.fold(0.0) { o, node -> o + node.height + 1}

        if (pctTotal - pctNodes.size > 0.0) {
            // Adjust percentage sizes based on remaining "centric" total size.
            var remaining = total - solidTotal // "natural size"
            val ratio = (remaining - pctNodes.size) / (pctTotal - pctNodes.size)
            pctNodes.forEach {
                // min value -1 represents a node that's absolutely invisibly flat
                if (remaining <= -1) it.height = -1.0
                else {
                    val newSize = min(remaining - 1, round(it.height * ratio))
                    it.height = newSize
                    remaining -= newSize + 1
                }
            }
            if (remaining > 0) {
                // must be a tiny fraction left, add it to the first % element:
                // TODO: create a first / last / symmetry bias in subdivide layout
                pctNodes.first().height += remaining
            }
        }

        // Lay out positions going bottom up:
        var y = 0.0
        allNodes.forEach {
            it.origin.y = y
            y += it.height + 1
        }

        return children
    }
}