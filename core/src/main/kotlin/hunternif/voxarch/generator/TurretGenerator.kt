package hunternif.voxarch.generator

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.sandbox.castle.turret.TurretPosition

/**
 * Adds child elements to make a Room look like a castle turret.
 */
@PublicGenerator("Turret")
class TurretGenerator : IGenerator {
    var roofShape: RoofShape = RoofShape.FLAT_BORDERED
    var bodyShape: BodyShape = BodyShape.SQUARE
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

    override fun generate(parent: DomBuilder<Node?>) {
        parent.asBuilder<Room>()?.apply {
            // Order matters! First apply the default styles, then the custom ones.
            newRoot(stylesheet + createTurretStyle(node)) {
                floor(BLD_FOUNDATION)
                polygonRoom(BLD_TURRET_BOTTOM)
                floor()
                allWalls {
                    wall(BLD_TOWER_BODY)
                    path(BLD_TOWER_CORBEL)
                    // TODO: place corbels as separate nodes
                }
                polygonRoom(BLD_TOWER_SPIRE, "roof")
                polygonRoom(BLD_TOWER_ROOF, "roof") {
                    floor(BLD_TOWER_ROOF)
                    allWalls { wall(BLD_TOWER_ROOF) }
                }
            }
        }
    }

    /**
     * These styles will apply to the new generated part of the DOM,
     * but not to any children.
     */
    private fun createTurretStyle(body: Room) = Stylesheet().apply {
        style(BLD_FOUNDATION) {
            visibleIf { hasFoundation() }
        }
        styleFor<PolygonRoom>(BLD_TURRET_BOTTOM) {
            shape { inherit() }
            visibleIf { hasTaperedBottom() }
            height { 2 * body.avgRadius() * taperRatio() }
            align {
                center()
                below()
            }
        }
        styleFor<PolygonRoom>("roof") {
            shape { inherit() }
            diameter { 100.pct + 2 * roofOffset() }
            align {
                center()
                above(1.vx) // 1 block above parent
            }
        }
        styleFor<PolygonRoom>(BLD_TOWER_SPIRE) {
            visibleIf { hasSpire() }
            height { 2 * (body.avgRadius() + roofOffset()) * spireRatio() }
        }
        styleFor<PolygonRoom>(BLD_TOWER_ROOF) {
            visibleIf { hasCrenellation() }
            height { 0.vx }
        }
        style(BLD_TOWER_CORBEL) {
            align { above() }
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
    private fun Room.avgRadius() = ((size.x + size.z) / 4).vx
    private fun roofOffset() = roofOffset.vx
    private fun spireRatio() = spireRatio
    private fun taperRatio() = taperRatio
}