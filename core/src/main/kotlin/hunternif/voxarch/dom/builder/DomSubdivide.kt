package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.dom.style.pct
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.centricToNatural
import hunternif.voxarch.plan.localSizeInDir
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import kotlin.math.min
import kotlin.math.round

/**
 * Subdivides space and lays out children along the given direction.
 * Ignores padding.
 * Rounds size to integer.
 */
class DomSubdivide(
    override var dir: Direction3D = EAST,
) : DomBuilder(), IDirectionBuilder {
    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        if (children.isEmpty()) return children

        val allNodes = ArrayList<Node>(children.size)
        val solidNodes = ArrayList<Node>(children.size)
        val pctNodes = ArrayList<Node>(children.size)
        children.forEach {
            if (it is StyledNode<*>) {
                allNodes.add(it.node)
                val props = it.ctx.stylesheet.getProperties(it)
                val prop = it.node.propertyForDir
                val size = props[prop]?.value ?: 100.pct
                if (size.isPct) pctNodes.add(it.node)
                else solidNodes.add(it.node)
            }
        }

        val parentNode = children.first().parentNode
        // "natural" size of the parent container
        val total = parentNode.localSizeInDir(dir).centricToNatural()

        // Get "natural" sizes pre-layout
        val solidTotal = solidNodes.fold(0.0) { o, node -> o + node.dirSize }
        val pctTotal = pctNodes.fold(0.0) { o, node -> o + node.dirSize }

        if (pctTotal > 0) {
            // Adjust percentage sizes based on remaining container size.
            var remaining = total - solidTotal
            val ratio = remaining / pctTotal
            pctNodes.forEach {
                if (remaining <= 0) it.dirSize = 0.0
                else {
                    val newSize = min(remaining, round(it.dirSize * ratio))
                    it.dirSize = newSize
                    remaining -= newSize
                }
            }
            if (remaining > 0) {
                // must be a tiny fraction left, add it to the first % element:
                // TODO: create a first / last / symmetry bias in subdivide layout
                pctNodes.first().dirSize += remaining
            }
        }

        layoutInDir(parentNode, allNodes)

        return children
    }

    private fun layoutInDir(parent: Node, nodes: Iterable<Node>) {
        var x = parent.initPos
        nodes.forEach {
            it.dirX = x
            x += sign * it.dirSize
        }
    }
}