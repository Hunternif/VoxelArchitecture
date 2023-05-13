package hunternif.voxarch.dom.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.naturalDepth
import hunternif.voxarch.plan.naturalWidth
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.TurretPosition
import kotlin.math.ceil

/**
 * Adds decorative child elements to make a Room look like a castle turret.
 */
class DomTurretDecor : DomBuilder() {
    // TODO: reset properties between runs
    var bottomShape: BottomShape = BottomShape.FLAT
    /** position of this turret in relation to parent turret */
    var positionType: TurretPosition = TurretPosition.NONE
    /** Angle vs parent turret. Usually facing away from the center. */
    val turretAngle: Double = 0.0
    /** Y/X slope of tapered bottom. */
    var taperRatio: Double = 1.5

    private val roofDecor = DomTurretRoofDecor()

    init {
        floor(BLD_FOUNDATION)
        polyRoom(BLD_TURRET_BOTTOM)
        floor()
        allWalls {
            wall(BLD_TOWER_BODY)
        }
        addChild(roofDecor)
    }

    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        roofDecor.addAllStyles(styleClass)
        return super.prepareForLayout(ctx)
    }

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        //TODO: make sure stylesheet is modified only once

        // Create style rules for this instance:
        ctx.stylesheet.add {
            styleFamily(selectDescendantOf(this@DomTurretDecor)) {
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
            offsetY { 1.vx }
        }
    }

    private fun hasFoundation() = bottomShape == BottomShape.FOUNDATION
    private fun hasTaperedBottom() = bottomShape == BottomShape.TAPERED
    private fun Node.avgRadius() = ceil((naturalWidth + naturalDepth) / 4).vx
    private fun taperRatio() = taperRatio
}