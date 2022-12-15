package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.turretDecor
import hunternif.voxarch.plan.PolyRoom

/** Adds child [PolyRoom] with a [DomTurretDecor]. */
open class DomPolyRoomWithTurretBuilder : DomPolyRoomBuilder() {
    override fun build(ctx: DomBuildContext) {
        turretDecor(*styleClass.toTypedArray())
        super.build(ctx)
    }
}
