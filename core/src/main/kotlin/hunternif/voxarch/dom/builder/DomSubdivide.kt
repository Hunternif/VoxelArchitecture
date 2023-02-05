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
        // + 1 to get "natural" size
        val total = parentNode.localSizeInDir(dir) + 1

        // Get "natural" sizes pre-layout
        val solidTotal = solidNodes.fold(0.0) { o, node -> o + node.dirSize + 1 }
        val pctTotal = pctNodes.fold(0.0) { o, node -> o + node.dirSize + 1 }

        if (pctTotal - pctNodes.size > 0.0) {
            // Adjust percentage sizes based on remaining "centric" total size.
            var remaining = total - solidTotal // "natural size"
            val ratio = (remaining - pctNodes.size) / (pctTotal - pctNodes.size)
            pctNodes.forEach {
                // min value -1 represents a node that's absolutely invisibly flat
                if (remaining <= -1) it.dirSize = -1.0
                else {
                    val newSize = min(remaining - 1, round(it.dirSize * ratio))
                    it.dirSize = newSize
                    remaining -= newSize + 1
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
            x += sign*(it.dirSize + 1)
        }
    }

    private val Node.propertyForDir: Property<Double>
        get() = when (dir.rotateY(rotationY)) {
            UP, DOWN -> PropHeight
            EAST, WEST -> PropWidth
            NORTH, SOUTH -> PropDepth
        }

    /** Node size in the direction [dir], accounting for its rotation. */
    private var Node.dirSize: Double
        get() = when (dir.rotateY(rotationY)) {
            UP, DOWN -> height
            EAST, WEST -> width
            NORTH, SOUTH -> depth
        }
        set(value) {
            when (dir.rotateY(rotationY)) {
                UP, DOWN -> height = value
                EAST, WEST -> width = value
                NORTH, SOUTH -> depth = value
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