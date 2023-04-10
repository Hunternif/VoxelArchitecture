package hunternif.voxarch.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.TorchStandBuilder
import hunternif.voxarch.sandbox.castle.builder.*

/*

This file contains all the default components of Voxel Architecture:
- Material names
- Node tags
- Default Builder instances

Material names should be mapped to specific voxel implementations.
Node tags should be mapped to specific builders instances.
Builder instances are used to build a given Node to concrete voxels. Using
default ones saves memory and helps to distinguish default vs custom behavior.

*/


// Materials
const val MAT_WALL = "wall"
const val MAT_WALL_DECORATION = "wall_decoration"
const val MAT_FLOOR = "floor"
const val MAT_ROOF = "roof"
const val MAT_TORCH = "torch"
const val MAT_POST = "post"

// Castle Node tags
const val BLD_FOUNDATION = "foundation"
const val BLD_TURRET_BOTTOM = "turret_bottom"
const val BLD_TOWER_MAIN = "tower_main"
const val BLD_CURTAIN_WALL = "curtain_wall"
const val BLD_TOWER_BODY = "tower_body"
const val BLD_TOWER_CORBEL = "tower_corbel"
const val BLD_TOWER_ROOF = "tower_roof"
const val BLD_TOWER_SPIRE = "tower_spire"
const val BLD_ARCHED_WINDOW = "arched_window"

// Builders
class DefaultBuilders {
    companion object {
        // Most basic builders
        val Node = Builder<Node>()
        val Floor = SimpleFloorBuilder(MAT_FLOOR)
        val Wall = SimpleWallBuilder(MAT_WALL)
        val Room = RoomBuilder()
        val Column = ColumnBuilder(MAT_WALL)
        val Gate = SimpleGateBuilder()
        val Hatch = SimpleHatchBuilder()
        val Fill = FillBuilder(MAT_WALL)

        // Castle builders
        val Foundation = FloorFoundationBuilder(MAT_WALL)
        val CastleCrenelGroundWall = CrenellationWallBuilder(MAT_WALL, downToGround = true)
        val CastleCrenelWall = CrenellationWallBuilder(MAT_WALL)
        val CastleCrenelPath = CrenellationPathBuilder(MAT_WALL)
        val CastleCorbel = CorbelBuilder(MAT_WALL_DECORATION)
        val CastleCrenelDecor = CrenellationWallBuilder(MAT_WALL_DECORATION)
        val PyramidRoof = PyramidBuilder(MAT_ROOF)
        val TowerTaperedBottom = PyramidBuilder(MAT_WALL, upsideDown = true)
        val ArchedWindow = ArchedWindowBuilder()

        // Special builders
        val SnakePath = SnakePathBuilder(MAT_WALL)
        val OneBlock = OneBlockPropBuilder(MAT_WALL_DECORATION)
        val TorchStand = TorchStandBuilder()
        val WallWithTorches = SimpleTorchlitWallBuilder(MAT_WALL)
    }
}

fun BuilderConfig.setDefaultBuilders() {
    setDefault<Floor>(DefaultBuilders.Floor)
    setDefault<Wall>(DefaultBuilders.Wall)
    setDefault<Column>(DefaultBuilders.Column)
    setDefault<Room>(DefaultBuilders.Room)
    setDefault<Gate>(DefaultBuilders.Gate)
    setDefault<Hatch>(DefaultBuilders.Hatch)
    setDefault<Node>(DefaultBuilders.Node)
}

fun BuilderConfig.setCastleBuilders() {
    set<Floor>(BLD_FOUNDATION to DefaultBuilders.Foundation)
    set<Wall>(BLD_TOWER_MAIN to DefaultBuilders.CastleCrenelWall)
    set<Wall>(BLD_CURTAIN_WALL to DefaultBuilders.CastleCrenelGroundWall)
    set<Wall>(BLD_TOWER_BODY to DefaultBuilders.Wall)
    set<Path>(BLD_TOWER_CORBEL to DefaultBuilders.CastleCorbel)
    set<Wall>(BLD_TOWER_ROOF to DefaultBuilders.CastleCrenelDecor)
    set<Wall>(BLD_ARCHED_WINDOW to DefaultBuilders.ArchedWindow)
    set<PolyRoom>(BLD_TOWER_SPIRE to DefaultBuilders.PyramidRoof)
    set<PolyRoom>(BLD_TURRET_BOTTOM to DefaultBuilders.TowerTaperedBottom)
}
