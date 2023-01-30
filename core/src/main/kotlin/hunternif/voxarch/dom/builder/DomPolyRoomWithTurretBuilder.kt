package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.PolyRoom

/** Adds child [PolyRoom] with a [DomTurretDecor]. */
open class DomPolyRoomWithTurretBuilder : DomPolyRoomBuilder() {

    private val decor: DomTurretDecor = DomTurretDecor()

    init {
        addChild(decor)
    }

    override fun prepareForLayout(ctx: DomBuildContext): StyledNode<PolyRoom> {
        decor.addAllStyles(styleClass)
        return super.prepareForLayout(ctx)
    }
}
