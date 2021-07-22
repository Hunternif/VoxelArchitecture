package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.turret.Turret

class DomTurretBuilder : DomNodeBuilder<Turret>({ Turret() }) {
    init {
        +BLD_TOWER_BODY
    }
    override val stylesheet by lazy { super.stylesheet + turretStyle }

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
            //TODO: implement material inheritance from classes, or set 'type'
        }
    }

    companion object {
        val turretStyle = Stylesheet().apply {
            styleFor<PolygonRoom>(BLD_TURRET_BOTTOM) {
                shape { inherit() }
            }
            //TODO: hide elements in style
        }
    }
}