package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.util.round

/**
 * Calls [block] in every corner of the [PolygonRoom]
 *
 * Will only work when added as a child to a [DomBuilder]<[PolygonRoom]>.
 */
open class DomLogicPolygonCornerBuilder(
    private val block: DomBuilder<Node?>.() -> Unit
) : DomLogicBuilder() {
    override fun build(): Node? {
        val room = parent.node
        if (room is PolygonRoom) {
            addCornerBuilders(room.polygon)
        }
        children.forEach { it.build() }
        return null
    }

    protected fun addCornerBuilders(polygon: Path) {
        polygon.points.forEachIndexed { i, offset ->
            val bld = DomTranslateBuilder(offset.round())
            addChild(bld, seed + 10000 + i)
            bld.block()
        }
    }
}

/**
 * Calls [block] in every corner of the [Room]
 *
 * Will only work when added as a child to a [DomBuilder]<[Room]>.
 */
class DomLogicFourCornerBuilder(
    block: DomBuilder<Node?>.() -> Unit
) : DomLogicPolygonCornerBuilder(block) {
    override fun build(): Node? {
        val room = parent.node
        if (room is Room) {
            val polygon = Path().apply {
                rectangle(room.width, room.length)
            }
            addCornerBuilders(polygon)
        }
        children.forEach { it.build() }
        return null
    }
}