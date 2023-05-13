package hunternif.voxarch.dom.builder

import kotlin.random.Random

/**
 * Selects 1 random child to run
 */
class DomRandom : DomBuilder() {
    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        if (children.isEmpty()) return emptyList()
        val child = children.random(Random(ctx.seed + seedOffset + 22000))
        return listOf(child)
    }
}