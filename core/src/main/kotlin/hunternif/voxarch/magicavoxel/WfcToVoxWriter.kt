package hunternif.voxarch.magicavoxel

import hunternif.voxarch.wfc.WfcColor
import hunternif.voxarch.wfc.tiled.WangTile
import hunternif.voxarch.wfc.tiled.WfcTiledModel
import java.nio.file.Path

/**
 * Writes from [WfcTiledModel] to a .vox file
 */
fun WfcTiledModel<WangTile>.writeToVoxFile(
    path: Path,
    colorMap: (WfcColor) -> VoxColor?
) {
    // Assuming all tiles are of equal size, fetch one to gauge grid size
    val samplePos = this.firstOrNull { this[it] != null }
    if (samplePos == null) {
        println("No data to write")
        return
    }
    val sampleTile = this[samplePos]!!
    val tx = sampleTile.width
    val ty = sampleTile.height
    val tz = sampleTile.depth

    val voxStorage = VoxFileStorage(
        width * tx,
        height * ty,
        depth * tz,
    )
    for (tilePos in this) {
        val tile = this[tilePos] ?: continue
        for (v in tile) {
            val voxel = tile[v]
            val p = v.add(tilePos.x * tx, tilePos.y * ty, tilePos.z * tz)
            voxStorage[p] = colorMap(voxel)
        }
    }

    println("writing WFC to $path")
    voxStorage.writeToFile(path)
}