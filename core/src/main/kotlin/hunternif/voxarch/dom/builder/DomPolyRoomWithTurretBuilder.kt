package hunternif.voxarch.dom.builder

import hunternif.voxarch.builder.BLD_TOWER_BODY
import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.PolyRoom

/** Adds child [PolyRoom] with a [DomTurretDecor]. */
open class DomPolyRoomWithTurretBuilder
    : DomPolyRoomBuilder<PolyRoom>(PolyRoom::class.java, { PolyRoom() }) {

    private val decor: DomTurretDecor = DomTurretDecor()

    init {
        addChild(decor)
        // The current node acts as the tower body, so we add style BLD_TOWER_BODY.
        addStyles(BLD_TOWER_BODY, DOM_TURRET)
    }

    override fun prepareForLayout(ctx: DomBuildContext): StyledNode<PolyRoom> {
        decor.addAllStyles(styleClass)
        return super.prepareForLayout(ctx)
    }
}
