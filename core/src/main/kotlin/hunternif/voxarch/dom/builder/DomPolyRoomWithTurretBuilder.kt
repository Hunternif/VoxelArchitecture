package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolyRoom

/** Adds child [PolyRoom] with a [DomTurretDecor]. */
open class DomPolyRoomWithTurretBuilder : DomPolyRoomBuilder() {

    private val decor: DomTurretDecor = DomTurretDecor()

    init {
        addChild(decor)
    }

    override fun build(ctx: DomBuildContext) {
        decor.addAllStyles(styleClass)
        super.build(ctx)
    }
}
