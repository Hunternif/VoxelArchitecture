package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuilderConfig
import hunternif.voxarch.sandbox.castle.builder.CorbelWallBuilder
import hunternif.voxarch.sandbox.castle.builder.CrenellationBuilder
import hunternif.voxarch.sandbox.castle.builder.FloorFoundationBuilder
import hunternif.voxarch.sandbox.castle.builder.PyramidBuilder

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
const val BLD_TOWER_ROOF = "tower_roof"
const val BLD_TOWER_SPIRE = "tower_spire"

/**
 * Register all the builders for my original castle style.
 */
fun BuilderConfig.setCastleBuilders() {
    set(BLD_FOUNDATION to FloorFoundationBuilder(MAT_WALL))
    set(BLD_TOWER_MAIN to CrenellationBuilder(MAT_WALL))
    set(BLD_CURTAIN_WALL to CrenellationBuilder(MAT_WALL, downToGround = true))
    set(BLD_FOUNDATION to FloorFoundationBuilder(MAT_WALL))
    set(BLD_TOWER_BODY to CorbelWallBuilder(
        MAT_WALL,
        MAT_WALL_DECORATION
    ))
    set(BLD_TOWER_ROOF to CrenellationBuilder(MAT_WALL_DECORATION))
    set(BLD_TOWER_SPIRE to PyramidBuilder(MAT_ROOF))
    set(BLD_TURRET_BOTTOM to PyramidBuilder(MAT_WALL, upsideDown = true))
}