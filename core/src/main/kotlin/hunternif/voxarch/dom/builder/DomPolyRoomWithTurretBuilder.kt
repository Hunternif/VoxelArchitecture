package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.turretDecor
import hunternif.voxarch.generator.GenTurretDecor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolyRoom

/** Adds child [PolyRoom] with a [GenTurretDecor]. */
open class DomPolyRoomWithTurretBuilder(ctx: DomContext) : DomPolyRoomBuilder(ctx) {
    override fun build(parentNode: Node) {
        turretDecor(*styleClass.toTypedArray())
        super.build(parentNode)
    }
}
