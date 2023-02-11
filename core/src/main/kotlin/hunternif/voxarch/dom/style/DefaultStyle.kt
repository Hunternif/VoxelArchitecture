package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall

val defaultStyle get() = Stylesheet().add {
    styleFor<Room> {
        diameter { 100.pct }
        height { 100.pct }
    }
    styleFor<Wall> {
        width { 100.pct }
        height { 100.pct }
    }
    styleFor<DomTurretDecor> {
        roofOffset { 1.vx }
        spireRatio { set(3.0) }
        taperRatio { set(1.5) }
    }
    style(DOM_TURRET) {
        snapOrigin { floorCenter() }
    }
}