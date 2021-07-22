package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom

/**
 * Calls [block] in every corner of the [PolygonRoom]
 *
 * Will only work when added as a child to a [DomBuilder]<[PolygonRoom]>.
 */
class DomLogicCornerBuilder(
    private val block: DomBuilder<Node?>.() -> Unit
) : DomLogicBuilder() {
    override fun build(): Node? {
        val room = parent.node
        if (room is PolygonRoom) {
            room.polygon.points.forEachIndexed { i, offset ->
                val bld = DomTranslateBuilder(offset)
                addChild(bld, seed + 10000 + i)
                bld.block()
            }
        }
        children.forEach { it.build() }
        return null
    }
}