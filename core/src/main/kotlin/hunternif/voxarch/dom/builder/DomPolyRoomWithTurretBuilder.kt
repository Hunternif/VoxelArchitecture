package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.turretDecor
import hunternif.voxarch.generator.GenTurretDecor
import hunternif.voxarch.plan.PolyRoom

/** Adds child [PolyRoom] with a [GenTurretDecor]. */
open class DomPolyRoomWithTurretBuilder(ctx: DomContext) : DomPolyRoomBuilder(ctx) {
    override fun build(bldCtx: DomBuildContext) {
        turretDecor(*styleClass.toTypedArray())
        super.build(bldCtx)
    }
}
