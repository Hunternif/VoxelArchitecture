package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall

val defaultStyle get() = Stylesheet().add {
    styleFor<Room> {
        width { 100.pct }
        height { 100.pct }
        depth { 100.pct }
    }
    styleFor<Wall> {
        width { 100.pct }
        height { 100.pct }
    }
    styleFor<DomTurretDecor> {
        roofOffset { 1.vx }
        spireRatio { set(2.5) }
        taperRatio { set(1.3) }
    }
    style(DOM_TURRET) {
        snapOrigin { floorCenter() }
    }
}