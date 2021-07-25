package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.turret.Turret

val defaultStyle get() = Stylesheet().apply {
    styleFor<Node> {
        diameter { 100.pct }
        height { 100.pct }
    }
    styleFor<Wall> {
        height { 100.pct }
    }
    styleFor<Turret> {
        roofOffset { 1.vx }
        spireRatio = 1.5
        taperRatio = 0.75
    }
}