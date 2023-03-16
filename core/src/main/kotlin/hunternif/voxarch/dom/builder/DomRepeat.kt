package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.builder.RepeatMode.*
import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import kotlin.math.max

/**
 * Repeats children along the given direction.
 *
 * These Style properties affect behavior:
 * * `gap:` gap between repeated items, in voxels.
 * * `repeat-mode:` strategy for filling the space, see [RepeatMode].
 */
class DomRepeat(
    override var dir: Direction3D = EAST,
) : DomBuilder(), IDirectionBuilder {
    /** Gap to be placed between children. */
    var gap: Int = 0

    /** What to do with remaining space. */
    var mode: RepeatMode = STRETCH

    /** Children are assumed to be at least this big.
     * Else repeating makes no sense. */
    var minSize: Double = 1.0

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        if (children.isEmpty()) return emptyList()
        // Need to create copies of children in advance, before layout.
        // 1. Measure
        val measuredChildren = children.mapIndexed { i, child ->
            val childCtx = ctx.copy(seed = ctx.seed + seedOffset + i + 1)
            child.prepareForLayout(childCtx).also {
                ctx.stylesheet.applyStyle(it)
                // immediately clean up so that the node is removed from parent:
                it.cleanup()
            }
        }

        // "natural" size of the parent container
        val totalSpace = gap + ctx.parentNode.localSizeInDir(dir).centricToNatural()

        val childrenWidth = measuredChildren
            .filterIsInstance<StyledNode<*>>()
            .fold(0.0) { o, child -> o + child.node.dirSize }

        val tileSize = gap + max(minSize, childrenWidth)
        val tileCount = (totalSpace / tileSize).toInt()
        val remainingSpace = totalSpace - tileCount * tileSize
        // TODO: snap to voxels?
        val extraTileSize = remainingSpace / tileCount
        val newTileSize = tileSize + extraTileSize

        // Lay out child nodes, adding extra space as needed
        val tileBuilders = mutableListOf<DomBuilder>()
        var x = ctx.parentNode.initPos
        for (i in 0 until tileCount) {
            val bld = DomRepeatTile(dir, x, newTileSize, extraTileSize, mode)
            bld.children.addAll(children)
            tileBuilders.add(bld)
            x += sign * newTileSize
        }

        return tileBuilders
    }
}

private class DomRepeatTile(
    override var dir: Direction3D,
    /** coordinate along the direction axis */
    private val dirX: Double,
    /** size along the direction axis */
    private val dirSize: Double,
    /** remaining size that must be filled */
    private val extraSize: Double,
    private val mode: RepeatMode,
) : DomBuilder(), IDirectionBuilder {

    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        val dummyNode = ctx.parentNode.run { node(start, size) }
        dummyNode.dirSize = dirSize
        dummyNode.dirX = dirX
        // TODO: apply mode
        // Using a generic StyledElement avoids calling styles on dummy node:
        return StyledElement(this, ctx.copy(parentNode = dummyNode))
    }

    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        if (children.isEmpty()) return children
        val ctx = children.first().ctx
        val dummyNode = ctx.parentNode
        dummyNode.collapse()
        return children
    }
}

enum class RepeatMode {
    /**
     * Children are repeated and rescaled (if necessary) to fill the area with
     * a whole number of tiles.
     */
    // TODO: in case of multiple children, maybe make some of them non-scalable?
    STRETCH,

    /**
     * Children are repeated to fill the area with a whole number of tiles,
     * and any extra space is distributed around the tiles.
     * I.e. each tile sits in the middle of its own box.
     */
    SPACE,
}