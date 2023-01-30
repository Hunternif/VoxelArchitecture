package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuilderConfig
import hunternif.voxarch.builder.SimpleWallBuilder
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.builder.*

// Materials
const val MAT_WALL = "wall"
const val MAT_WALL_DECORATION = "wall_decoration"
const val MAT_FLOOR = "floor"
const val MAT_ROOF = "roof"
const val MAT_TORCH = "torch"
const val MAT_POST = "post"

// Building node types
const val BLD_FOUNDATION = "foundation"
const val BLD_TURRET_BOTTOM = "turret_bottom"
const val BLD_TOWER_MAIN = "tower_main"
const val BLD_CURTAIN_WALL = "curtain_wall"
const val BLD_TOWER_BODY = "tower_body"
const val BLD_TOWER_CORBEL = "tower_corbel"
const val BLD_TOWER_ROOF = "tower_roof"
const val BLD_TOWER_SPIRE = "tower_spire"

const val BLD_ARCHED_WINDOW = "arched_window"

/**
 * Register all the builders for my original castle style.
 */
fun BuilderConfig.setCastleBuilders() {
    set(BLD_FOUNDATION to FloorFoundationBuilder(MAT_WALL))
    set<Wall>(BLD_TOWER_MAIN to CrenellationWallBuilder(MAT_WALL))
    set<Wall>(BLD_CURTAIN_WALL to CrenellationWallBuilder(MAT_WALL, downToGround = true))
    set<Wall>(BLD_TOWER_BODY to SimpleWallBuilder(MAT_WALL))
    set<Path>(BLD_TOWER_CORBEL to CorbelBuilder(MAT_WALL_DECORATION))
    set<Wall>(BLD_TOWER_ROOF to CrenellationWallBuilder(MAT_WALL_DECORATION))
    set<Wall>(BLD_ARCHED_WINDOW to ArchedWindowBuilder())
    set(BLD_TOWER_SPIRE to PyramidBuilder(MAT_ROOF))
    set(BLD_TURRET_BOTTOM to PyramidBuilder(MAT_WALL, upsideDown = true))
}