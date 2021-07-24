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
        +BLD_TOWER_BODY
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
        polygonRoom(BLD_TOWER_SPIRE)
        polygonRoom(BLD_TOWER_ROOF) {
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
        }
        styleFor<PolygonRoom>(BLD_TOWER_SPIRE) {
            shape { inherit() }
            visibleIf { t.hasSpire() }
            y { 100.pct + 1.vx }
            diameter { 100.pct + 2 * t.roofOffset() }
            height { 2 * (t.avgRadius() + t.roofOffset()) * t.spireRatio() }
        }
        styleFor<PolygonRoom>(BLD_TOWER_ROOF) {
            shape { inherit() }
            visibleIf { t.hasCrenellation() }
            y { 100.pct + 1.vx } // 1 block above parent
            diameter { 100.pct + 2 * t.roofOffset() }
            height { 0.vx }
        }
        style(BLD_TOWER_CORBEL) {
            y { 100.pct }
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
}