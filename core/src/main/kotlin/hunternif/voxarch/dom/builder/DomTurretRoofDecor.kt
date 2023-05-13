package hunternif.voxarch.dom.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.naturalDepth
import hunternif.voxarch.plan.naturalWidth
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import kotlin.math.ceil

/**
 * Adds decorative child elements on the roof to make a node look like a castle turret.
 */
class DomTurretRoofDecor : DomBuilder() {
    // TODO: reset properties between runs
    var roofShape: RoofShape = RoofShape.FLAT_BORDERED
    /** Offset for roof border and spire. */
    var roofOffset: Int = 1
    /** Y/X slope of roof spire. */
    var spireRatio: Double = 3.0

    init {
        allWalls {
            path(BLD_TOWER_CORBEL)
            // TODO: place corbels as separate nodes
        }
        polyRoom(BLD_TOWER_SPIRE, "roof")
        polyRoom(BLD_TOWER_ROOF, "roof") {
            floor(BLD_TOWER_ROOF)
            allWalls { wall(BLD_TOWER_ROOF) }
        }
    }

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        //TODO: make sure stylesheet is modified only once

        // Create style rules for this instance:
        ctx.stylesheet.add {
            styleFamily(selectDescendantOf(this@DomTurretRoofDecor)) {
                addTurretStyle(ctx.parentNode)
            }
        }
        return super.getChildrenForLayout(ctx)
    }

    /**
     * These styles will apply to the new generated part of the DOM,
     * but not to any children. The unique class name at the root ensures that.
     */
    private fun RuleBuilder.addTurretStyle(body: Node) {
        styleFor<PolyRoom>("roof") {
            shape { inherit() }
            diameter { 100.pct + 2 * roofOffset() }
            alignXZ { center() }
            alignY { above() }
        }
        styleFor<PolyRoom>(BLD_TOWER_SPIRE) {
            visibleIf { hasSpire() }
            height { (body.avgRadius() + roofOffset()) * spireRatio() }
        }
        styleFor<PolyRoom>(BLD_TOWER_ROOF) {
            visibleIf { hasCrenellation() }
            height { 1.vx }
        }
        style(BLD_TOWER_CORBEL) {
            alignY { top() }
        }
    }

    private fun hasSpire() = when (roofShape) {
        RoofShape.SPIRE, RoofShape.SPIRE_BORDERED -> true
        else -> false
    }
    private fun hasCrenellation() = when (roofShape) {
        RoofShape.FLAT_BORDERED, RoofShape.SPIRE_BORDERED -> true
        else -> false
    }
    private fun Node.avgRadius() = ceil((naturalWidth + naturalDepth) / 4).vx
    private fun roofOffset() = roofOffset.vx
    private fun spireRatio() = spireRatio
}