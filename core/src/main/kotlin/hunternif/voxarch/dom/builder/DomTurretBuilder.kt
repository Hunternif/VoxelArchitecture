package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.turret.BottomShape.*
import hunternif.voxarch.sandbox.castle.turret.RoofShape.*
import hunternif.voxarch.sandbox.castle.turret.Turret

class DomTurretBuilder : DomNodeBuilder<Turret>({ Turret() }) {
    init {
        // The [node] here acts as the tower body, so we apply style to it:
        addStyle(BLD_TOWER_BODY)
    }
    override val stylesheet by lazy {
        // Order matters! First apply the default styles, then the custom ones.
        defaultStyle + super.stylesheet + turretStyle
    }
    private val turret = node

    override fun buildNode() {
        node.createPolygon()
        addTurretParts()
    }

    private fun addTurretParts() {
        floor(BLD_FOUNDATION)
        polygonRoom(BLD_TURRET_BOTTOM)
        floor()
        allWalls {
            wall()
            path(BLD_TOWER_CORBEL)
            // TODO: place corbels as separate nodes
        }
        polygonRoom(BLD_TOWER_SPIRE, "roof")
        polygonRoom(BLD_TOWER_ROOF, "roof") {
            floor()
            allWalls { wall() }
        }
    }

    private val turretStyle = Stylesheet().apply {
        val t = this@DomTurretBuilder
        style(BLD_FOUNDATION) {
            visibleIf { t.hasFoundation() }
        }
        styleFor<PolygonRoom>(BLD_TURRET_BOTTOM) {
            shape { inherit() }
            visibleIf { t.hasTaperedBottom() }
            height { 2 * t.avgRadius() * t.taperRatio() }
            align { below() }
        }
        styleFor<PolygonRoom>("roof") {
            shape { inherit() }
            diameter { 100.pct + 2 * t.roofOffset() }
            align { above(1.vx) } // 1 block above parent
        }
        styleFor<PolygonRoom>(BLD_TOWER_SPIRE) {
            visibleIf { t.hasSpire() }
            height { 2 * (t.avgRadius() + t.roofOffset()) * t.spireRatio() }
        }
        styleFor<PolygonRoom>(BLD_TOWER_ROOF) {
            visibleIf { t.hasCrenellation() }
            height { 0.vx }
        }
        style(BLD_TOWER_CORBEL) {
            align { above() }
        }
    }

    private fun hasFoundation() = turret.bottomShape == FOUNDATION
    private fun hasTaperedBottom() = turret.bottomShape == TAPERED
    private fun hasSpire() = when (turret.roofShape) {
        SPIRE, SPIRE_BORDERED -> true
        else -> false
    }
    private fun hasCrenellation() = when (turret.roofShape) {
        FLAT_BORDERED, SPIRE_BORDERED -> true
        else -> false
    }
    private fun avgRadius() = ((turret.size.x + turret.size.z) / 4).vx
    private fun roofOffset() = turret.roofOffset.vx
    private fun spireRatio() = turret.spireRatio
    private fun taperRatio() = turret.taperRatio
}