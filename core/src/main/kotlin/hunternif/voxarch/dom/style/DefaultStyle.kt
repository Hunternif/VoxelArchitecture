package hunternif.voxarch.dom.style

import hunternif.voxarch.builder.BLD_ARCHED_BRIDGE
import hunternif.voxarch.builder.BLD_SPACE
import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.builder.DomTurretBottomDecor
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.builder.DomTurretRoofDecor
import hunternif.voxarch.plan.*

val defaultStyle get() = Stylesheet().add {
    style(
        select(Room::class.java, Slope::class.java, SlopedRoof::class.java),
        select(BLD_SPACE)
    ) {
        width { 100.pct }
        height { 100.pct }
        depth { 100.pct }
        alignXZ { center() }
    }
    styleFor<Wall> {
        width { 100.pct }
        height { 100.pct }
    }
    styleFor<Window> {
        width { 100.pct }
        height { 100.pct }
        // Extra depth to punch through misaligned walls
        depth { 100.pct + 2.vx }
        alignXZ { center() }
    }
    styleFor<DomTurretRoofDecor> {
        roofOffset { 1.vx }
        spireRatio { set(2.5) }
    }
    styleFor<DomTurretBottomDecor> {
        taperRatio { set(1.3) }
    }
    style(DOM_TURRET) {
        snapOrigin { floorCenter() }
    }
    styleFor<Column> {
        height { 100.pct }
        snapOrigin { floorCenter() }
    }
    style(BLD_ARCHED_BRIDGE) {
        depth { 100.pct }
    }
}