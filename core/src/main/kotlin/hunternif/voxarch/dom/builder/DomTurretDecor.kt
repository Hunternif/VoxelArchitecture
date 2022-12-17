package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.sandbox.castle.turret.TurretPosition

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
    /** Offset for borders and spires in all child turrets. */
    var roofOffset: Int = 1
    /** Y/X ratio of spires for all child turrets. */
    var spireRatio: Double = 1.5
    /** Y/X ratio of tapered bottoms of turrets. */
    var taperRatio: Double = 0.75

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

    override fun build(ctx: DomBuildContext) {
        onlyOnce {
            // The unique class name ensures that the following style rules
            // will only apply to this turret instance:
            val uniqueClass = "turret_decor_${hashCode()}"
            addStyle(uniqueClass)
            // Create style rules for this instance:
            ctx.stylesheet.add {
                styleFamily(selectInherit(uniqueClass)) {
                    addTurretStyle(ctx.parentNode)
                }
            }
        }
        super.build(ctx)
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
            height { 2 * body.avgRadius() * taperRatio() }
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
            height { 2 * (body.avgRadius() + roofOffset()) * spireRatio() }
        }
        styleFor<PolyRoom>(BLD_TOWER_ROOF) {
            visibleIf { hasCrenellation() }
            height { 0.vx }
        }
        style(BLD_TOWER_CORBEL) {
            alignY { above() }
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
    private fun Node.avgRadius() = ((size.x + size.z) / 4).vx
    private fun roofOffset() = roofOffset.vx
    private fun spireRatio() = spireRatio
    private fun taperRatio() = taperRatio
}