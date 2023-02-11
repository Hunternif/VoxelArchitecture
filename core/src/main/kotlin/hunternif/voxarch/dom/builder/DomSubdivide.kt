package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.rotateY
import kotlin.math.min
import kotlin.math.round

/**
 * Subdivides space and lays out children along the given direction.
 * Rounds size to integer.
 */
class DomSubdivide(private var dir: Direction3D = UP) : DomBuilder() {
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
        val initPos = when (dir) {
            UP -> parent.start.y
            EAST -> parent.start.x
            SOUTH -> parent.start.z
            DOWN -> parent.start.y + parent.height
            WEST -> parent.start.x + parent.width
            NORTH -> parent.start.z + parent.depth
        }
        val sign = when (dir) {
            UP, EAST, SOUTH -> 1.0
            DOWN, WEST, NORTH -> -1.0
        }

        var x = initPos
        nodes.forEach {
            it.dirX = x
            x += sign * it.dirSize
        }
    }

    private val Node.propertyForDir: Property<Double>
        get() = when (dir.rotateY(rotationY)) {
            UP, DOWN -> PropHeight
            EAST, WEST -> PropWidth
            NORTH, SOUTH -> PropDepth
        }

    /** Node "natural" size in the direction [dir], accounting for its rotation. */
    private var Node.dirSize: Double
        get() = when (dir.rotateY(rotationY)) {
            UP, DOWN -> naturalHeight
            EAST, WEST -> naturalWidth
            NORTH, SOUTH -> naturalDepth
        }
        set(value) {
            when (dir.rotateY(rotationY)) {
                UP, DOWN -> naturalHeight = value
                EAST, WEST -> naturalWidth = value
                NORTH, SOUTH -> naturalDepth = value
            }
        }

    /** Starting coordinate in the direction [dir] */
    private var Node.dirX: Double
        get() = when (dir) {
            UP -> minY
            DOWN -> maxY
            EAST -> minX
            WEST -> maxX
            SOUTH -> minZ
            NORTH -> maxZ
        }
        set(value) {
            when (dir) {
                UP -> minY = value
                DOWN -> maxY = value
                EAST -> minX = value
                WEST -> maxX = value
                SOUTH -> minZ = value
                NORTH -> maxZ = value
            }
        }
}