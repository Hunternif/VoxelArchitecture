package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall

val defaultStyle get() = Stylesheet().apply {
    styleFor<Room> {
        diameter { 100.pct }
        height { 100.pct }
    }
    styleFor<Wall> {
        height { 100.pct }
    }
    styleFor<PolygonRoom>(DOM_TURRET) {
        roofOffset { 1.vx }
        spireRatio = 1.5
        taperRatio = 0.75
    }
}