package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.plan.innerFloorCenter
import hunternif.voxarch.util.clampMin
import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.util.roundToEven
import kotlin.math.*

open class DomPolyRoomBuilder<N: PolyRoom>(
    nodeClass: Class<N>,
    createNode: () -> N,
) : DomNodeBuilder<N>(nodeClass, createNode) {
    companion object {
        inline operator fun <reified N : PolyRoom> invoke(
            noinline createNode: () -> N,
        ): DomPolyRoomBuilder<N> =
            DomPolyRoomBuilder(N::class.java, createNode)

        operator fun invoke(): DomPolyRoomBuilder<PolyRoom> =
            DomPolyRoomBuilder(PolyRoom::class.java) { PolyRoom() }
    }

    override fun postLayout(element: StyledNode<N>) =
        element.node.createPolygon()
}

internal fun PolyRoom.createPolygon() {
    polygon.origin = innerFloorCenter
    when (shape) {
        PolyShape.SQUARE -> polygon.rectangle(width, depth)
        PolyShape.ROUND -> {
            val radius = (width + depth) / 2
            val sideCount = when {
                sideCount >= 3 -> sideCount
                edgeLength >= 1.0 -> countChordsGivenLength(edgeLength, radius)
                    .clampMin(4).roundToEven()
                else -> ceil(radius * 0.334).toInt() * 4
            }
            polygon.ellipse(width, depth, sideCount)
        }
        PolyShape.OCTAGON -> polygon.ellipse(width, depth, 8)
    }
}

/** Counts how many chords of length [length] fit in a circle of radius [radius] */
internal fun countChordsGivenLength(length: Double, radius: Double): Int {
    val r = max(1.0, radius)
    val len = max(1.0, length)
    return round(PI / asin(len / 2 / r)).toInt()
}