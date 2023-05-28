package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.builder.RepeatMode.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.rotateY
import kotlin.math.max

/**
 * Repeats children along the given direction.
 *
 * These Style properties affect behavior:
 * * `gap:` gap between repeated items, in voxels.
 * * `repeat-mode:` strategy for filling the space, see [RepeatMode].
 * * `minSize:` minimum tile size.
 */
class DomRepeat(
    override var dir: Direction3D = EAST,
) : DomBuilder(), IDirectionBuilder {
    /** Gap to be placed between children. */
    var gap: Double = 0.0

    /** What to do with remaining space. */
    var mode: RepeatMode = OFF

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
        val totalSpace = ctx.parentNode.localSizeInDir(dir).centricToNatural()

        val childrenWidth = measuredChildren
            .filterIsInstance<StyledNode<*>>()
            .fold(0.0) { o, child -> o + child.node.dirSize }

        var gap = gap
        val tileSize = max(minSize, childrenWidth)
        val tileCount = ((totalSpace + gap) / (tileSize + gap)).toInt()
        var remainingSpace = (totalSpace + gap) - tileCount * (tileSize + gap)

        // If spacing items apart, stretch the gap between them:
        if (mode == SPACE && tileCount > 1) {
            gap = (totalSpace - tileSize * tileCount) / (tileCount - 1)
            remainingSpace = 0.0
        }

        // TODO: snap to voxels?
        val newTileSize = tileSize + remainingSpace / tileCount

        // Lay out child nodes, adding extra space as needed
        val tileBuilders = mutableListOf<DomBuilder>()
        var x = ctx.parentNode.initPos
        for (i in 0 until tileCount) {
            val bld = DomRepeatTile(dir, x, newTileSize, mode)
            bld.children.addAll(children)
            tileBuilders.add(bld)
            x += sign * (newTileSize + gap)
        }

        //TODO: maybe apply aspect ratio?

        return tileBuilders
    }
}

private class DomRepeatTile(
    override var dir: Direction3D,
    /** coordinate along the direction axis */
    private val dirX: Double,
    /** size along the direction axis */
    private val dirSize: Double,
    private val mode: RepeatMode,
) : DomBuilder(), IDirectionBuilder {

    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        val dummyNode = ctx.parentNode.run { node(start, size) }
        dummyNode.dirSize = dirSize
        dummyNode.dirX = dirX
        dummyNode.tags.add("_dummy_")
        ctx.stats.dummyNodes.add(dummyNode)
        // Using a generic StyledElement avoids calling styles on dummy node:
        return StyledElement(this, ctx.copy(parentNode = dummyNode))
    }

    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        if (children.isEmpty()) return children

        when (mode) {
            OFF -> {}
            SPACE -> {
                // Align children in the middle of the tile:
                val rule = Rule().apply {
                    when (dir) {
                        UP, DOWN -> {}
                        EAST, WEST -> alignX { center() }
                        SOUTH, NORTH -> alignZ { center() }
                    }
                }
                for (decl in rule.declarations) {
                    children.forEach { decl.applyTo(it) }
                }
            }
            STRETCH -> {
                // Can't use style because each child could be rotated:
                children.filterIsInstance<StyledNode<*>>().forEach {
                    val childDir = dir.rotateY(it.node.rotationY)
                    val parentSize = it.parentNode.localSizeInDir(dir).centricToNatural()
                    when (childDir) {
                        UP, DOWN -> {}
                        EAST, WEST -> it.node.naturalWidth = parentSize
                        SOUTH, NORTH -> it.node.naturalDepth = parentSize
                    }
                }
            }
        }

        return children
    }
}

enum class RepeatMode {
    /**
     * Lets children handle its own position via their own styles.
     */
    OFF,

    /**
     * Children are repeated and rescaled (if necessary) to fill the area with
     * a whole number of tiles.
     */
    // TODO: in case of multiple children, maybe make some of them non-scalable?
    STRETCH,

    /**
     * Children are repeated to fill the area with a whole number of tiles,
     * and any extra space is distributed between the tiles, widening the gap.
     * The tiles at each end of the line will stick to that end.
     */
    SPACE,
}