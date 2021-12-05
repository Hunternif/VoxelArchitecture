package hunternif.voxarch.magicavoxel

import com.scs.voxlib.VoxWriter
import hunternif.voxarch.wfc.WangTile
import hunternif.voxarch.wfc.WangVoxel
import hunternif.voxarch.wfc.WfcGrid
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Writes from [WfcGrid] to a .vox file
 */
fun WfcGrid<WangTile>.writeToVoxFile(
    path: Path,
    colorMap: Map<WangVoxel, VoxColor?>
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
    val tz = sampleTile.length

    val voxStorage = VoxFileStorage(
        width * tx,
        height * ty,
        length * tz
    )
    for (tilePos in this) {
        val tile = this[tilePos] ?: continue
        for (v in tile) {
            val voxel = tile[v]
            val p = v.add(tilePos.x * tx, tilePos.y * ty, tilePos.z * tz)
            voxStorage[p] = colorMap[voxel]
        }
    }

    println("writing WFC to $path")
    voxStorage.writeToFile(path)
}

fun VoxFileStorage.writeToFile(path: Path) {
    val voxFile = this.serialize()
    val stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    VoxWriter(stream).use { it.write(voxFile) }
}