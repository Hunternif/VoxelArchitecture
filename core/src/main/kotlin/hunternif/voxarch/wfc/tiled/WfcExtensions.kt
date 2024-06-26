package hunternif.voxarch.wfc.tiled


/** Collapse tiles at the edge of the grid to be [air]. */
fun <T: WfcTile> WfcTiledModel<T>.setAirBoundary(air: T) = setAirAndGroundBoundary(air, air, air)

/**
 * Collapse tiles at the edge of the grid to be [air],
 * on the bottom Y layer to be [ground],
 * and on the perimeter at y=1 to be [groundedAir].
 * "Grounded air" means "ground below + air above".
 */
fun <T: WfcTile> WfcTiledModel<T>.setAirAndGroundBoundary(air: T, groundedAir: T, ground: T) {
    for (p in this) {
        if (p.y >= height-1) this[p] = air
        else if (p.y <= 0) this[p] = ground
        else if (p.x <= 0 || p.x >= width-1 ||
            p.z <= 0 || p.z >= depth-1) {
            if (p.y <= 1) this[p] = groundedAir
            else this[p] = air
        }
    }
    resetPropagationTo(1, 0, 1)
    propagate()
}