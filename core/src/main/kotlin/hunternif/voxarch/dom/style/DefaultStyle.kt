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
    style2For<TurretGenerator> {
        roofOffset2 { 1.vx }
        spireRatio2 { set(1.5) }
        taperRatio2 { set(0.75) }
    }
}