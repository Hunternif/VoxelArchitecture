package hunternif.voxarch.dom.builder

import hunternif.voxarch.builder.BLD_TOWER_BODY
import hunternif.voxarch.dom.allWalls
import hunternif.voxarch.dom.floor
import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.wall

/**
 * Adds decorative child elements to make a Room look like a castle turret.
 */
class DomTurretDecor : DomBuilder() {
    // TODO: reset properties between runs
    /** Angle vs parent turret. Usually facing away from the center. */
    val turretAngle: Double = 0.0

    private val bottomDecor = DomTurretBottomDecor()
    private val roofDecor = DomTurretRoofDecor()

    init {
        addChild(bottomDecor)
        floor()
        allWalls {
            wall(BLD_TOWER_BODY)
        }
        addChild(roofDecor)
    }

    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        bottomDecor.addAllStyles(styleClass)
        roofDecor.addAllStyles(styleClass)
        return super.prepareForLayout(ctx)
    }
}