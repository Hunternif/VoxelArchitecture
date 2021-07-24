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
        super.stylesheet + turretStyle(node)
    }

    override fun buildNode() {
        addTurretParts()
        addCorbels()
    }

    private fun addCorbels() = node.run {
        // corbels
        walls.forEach {
            it.path(size.y) {
                type = BLD_TOWER_CORBEL
            }
        }
        // TODO: place corbels as separate nodes
    }

    private fun addTurretParts() {
        floor(BLD_FOUNDATION)
        polygonRoom(BLD_TURRET_BOTTOM)
        floor()
        allWalls { wall() }
        polygonRoom(BLD_TOWER_SPIRE)
        polygonRoom(BLD_TOWER_ROOF) {
            floor()
            allWalls { wall() }
        }
    }

    private fun turretStyle(turret: Turret) = Stylesheet().apply {
        fun hasFoundation() = turret.bottomShape == FOUNDATION
        fun hasTaperedBottom() = turret.bottomShape == TAPERED
        fun hasSpire() = when (turret.roofShape) {
            SPIRE, SPIRE_BORDERED -> true
            else -> false
        }
        fun hasCrenellation() = when (turret.roofShape) {
            FLAT_BORDERED, SPIRE_BORDERED -> true
            else -> false
        }

        styleFor<Floor>(BLD_FOUNDATION) {
            visibleIf { hasFoundation() }
        }
        styleFor<PolygonRoom>(BLD_TURRET_BOTTOM) {
            shape { inherit() }
            visibleIf { hasTaperedBottom() }
        }
        styleFor<PolygonRoom>(BLD_TOWER_SPIRE) {
            shape { inherit() }
            visibleIf { hasSpire() }
        }
        styleFor<PolygonRoom>(BLD_TOWER_ROOF) {
            shape { inherit() }
            visibleIf { hasCrenellation() }
            y { 100.pct + 1.vx } // 1 block above parent
        }
        return this
    }
}