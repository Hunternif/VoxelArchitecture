package hunternif.voxarch.dom.style

import hunternif.voxarch.generator.GenTurretDecor
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
    styleFor<GenTurretDecor> {
        roofOffset { 1.vx }
        spireRatio { set(1.5) }
        taperRatio { set(0.75) }
    }
}