package hunternif.voxarch.dom.style

import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall

val defaultStyle get() = Stylesheet().apply {
    style2For<Room> {
        diameter2 { 100.pct }
        height2 { 100.pct }
    }
    style2For<Wall> {
        height2 { 100.pct }
    }
    styleForGen<TurretGenerator> {
        roofOffset { 1.vx }
        spireRatio = 1.5
        taperRatio = 0.75
    }
}