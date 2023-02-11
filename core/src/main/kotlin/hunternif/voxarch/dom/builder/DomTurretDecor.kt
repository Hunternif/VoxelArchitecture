package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.naturalDepth
import hunternif.voxarch.plan.naturalWidth
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.sandbox.castle.turret.TurretPosition
import kotlin.math.ceil

/**
 * Adds decorative child elements to make a Room look like a castle turret.
 */
class DomTurretDecor : DomBuilder() {
    // TODO: reset properties between runs
    var roofShape: RoofShape = RoofShape.FLAT_BORDERED
    var bottomShape: BottomShape = BottomShape.FLAT
    /** position of this turret in relation to parent turret */
    var positionType: TurretPosition = TurretPosition.NONE
    /** Angle vs parent turret. Usually facing away from the center. */
    val turretAngle: Double = 0.0
    /** Offset for roof border and spire. */
    var roofOffset: Int = 1
    /** Y/X slope of roof spire. */
    var spireRatio: Double = 3.0
    /** Y/X slope of tapered bottom. */
    var taperRatio: Double = 1.5

    init {
        floor(BLD_FOUNDATION)
        polyRoom(BLD_TURRET_BOTTOM)
        floor()
        allWalls {
            wall(BLD_TOWER_BODY)
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
            styleFamily(selectInherit(this@DomTurretDecor)) {
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
        style(BLD_FOUNDATION) {
            visibleIf { hasFoundation() }
        }
        styleFor<PolyRoom>(BLD_TURRET_BOTTOM) {
            shape { inherit() }
            visibleIf { hasTaperedBottom() }
            height { body.avgRadius() * taperRatio() }
            alignXZ { center() }
            alignY { below() }
        }
        styleFor<PolyRoom>("roof") {
            shape { inherit() }
            diameter { 100.pct + 2 * roofOffset() }
            alignXZ { center() }
            alignY { above() }
            offsetY { 1.vx } // 1 block above parent
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

    private fun hasFoundation() = bottomShape == BottomShape.FOUNDATION
    private fun hasTaperedBottom() = bottomShape == BottomShape.TAPERED
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
    private fun taperRatio() = taperRatio
}