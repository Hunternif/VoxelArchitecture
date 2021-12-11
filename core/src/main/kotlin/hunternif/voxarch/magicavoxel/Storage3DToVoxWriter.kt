package hunternif.voxarch.magicavoxel

import com.scs.voxlib.VoxWriter
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.forEachIndexed
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Writes from any storage to a .vox file
 */
fun <T> IStorage3D<T?>.writeToVoxFile(
    path: Path,
    colorMap: Map<T, VoxColor?>
) {
    val voxStorage = VoxFileStorage(width, height, length)
    forEachIndexed { p, color ->
        if (color != null)
            voxStorage[p] = colorMap[color]
    }
    println("writing to $path")
    voxStorage.writeToFile(path)
}

fun VoxFileStorage.writeToFile(path: Path) {
    val voxFile = this.serialize()
    val stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    VoxWriter(stream).use { it.write(voxFile) }
}