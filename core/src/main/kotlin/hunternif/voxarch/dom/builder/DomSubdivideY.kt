package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.dom.style.pct
import hunternif.voxarch.dom.style.property.PropHeight
import kotlin.math.min
import kotlin.math.round

/**
 * Subdivides space and lays out children along the Y direction.
 * Rounds size to integer.
 */
class DomSubdivideY : DomBuilder() {
    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        val items = children
            .filterIsInstance<StyledNode<*>>()
            .associateWith {
                val props = it.ctx.stylesheet.getProperties(it)
                props[PropHeight]?.value ?: 100.pct
            }
        if (items.isEmpty()) return children

        val parentNode = items.keys.first().parentNode
        // + 1 to get "natural" size
        val total = parentNode.height + 1

        // Get "natural" sizes pre-layout
        var solidTotal = 0.0 // total "natural" size of solid nodes
        var pctTotal = 0.0   // total "natural" size of % nodes
        var solidCount = 0   // number of solid nodes
        var pctCount = 0     // number of % nodes
        items.forEach { (k, v) ->
            if (v.isPct) {
                pctTotal += k.node.height + 1
                pctCount++
            } else {
                solidTotal += k.node.height + 1
                solidCount++
            }
        }

        if (pctTotal - pctCount > 0.0) {
            // Adjust percentage sizes based on remaining "centric" total size.
            // (- pctCount because each node will occupy 1 extra block)
            var remaining = total - solidTotal // "natural size"
            val ratio = (remaining - pctCount) / (pctTotal - pctCount)
            items.forEach { (k, v) ->
                if (v.isPct) {
                    // min value -1 represents a node that's absolutely invisibly flat
                    if (remaining <= -1) k.node.height = -1.0
                    else {
                        val newSize = min(remaining - 1, round(k.node.height * ratio))
                        k.node.height = newSize
                        remaining -= newSize + 1
                    }
                }
            }
            if (remaining > 0) {
                // must be a tiny fraction left, add it to the first % element:
                // TODO: create a first / last / symmetry bias in subdivide layout
                val firstPctEntry = items.entries.first { (k, v) -> v.isPct}
                firstPctEntry.key.node.height += remaining
            }
        }

        // Lay out positions going bottom up:
        var y = 0.0
        items.keys.forEach {
            it.node.origin.y = y
            y += it.node.height + 1
        }

        return children
    }
}