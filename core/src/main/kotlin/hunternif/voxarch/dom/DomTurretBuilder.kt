package hunternif.voxarch.dom

import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.turret.Turret

class DomTurretBuilder(
    styleClass: Collection<String>,
    parent: DomBuilder<Node?>,
    seed: Long
) : DomNodeBuilder<Turret>(
    styleClass + BLD_TOWER_BODY,
    parent,
    seed,
    { Turret() }
) {
    override val stylesheet by lazy { super.stylesheet + turretStyle }

    override fun build(): Turret {
        findParentNode().addChild(node)
        stylesheet.apply(this, styleClass)
        addTurretParts()
        addCorbels()
        children.forEach { it.build() }
        return node
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
        walls()
        polygonRoom(BLD_TOWER_SPIRE)
        polygonRoom(BLD_TOWER_ROOF) {
            floor()
            walls()
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