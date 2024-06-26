package hunternif.voxarch.magicavoxel

import com.scs.voxlib.VoxWriter
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.forEachPos
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Writes from any storage to a .vox file
 */
fun <T> IStorage3D<out T?>.writeToVoxFile(
    path: Path,
    colorMap: (T) -> VoxColor?
) {
    val voxStorage = VoxFileStorage()
    forEachPos { x, y, z, color ->
        if (color != null)
            voxStorage[x, y, z] = colorMap(color)
    }
    println("writing to $path")
    voxStorage.writeToFile(path)
}

fun IStorage3D<VoxColor?>.writeToVoxFile(path: Path) {
    val voxStorage = VoxFileStorage()
    forEachPos { x, y, z, color ->
        if (color != null)
            voxStorage[x, y, z] = color
    }
    println("writing to $path")
    voxStorage.writeToFile(path)
}

fun VoxFileStorage.writeToFile(path: Path) {
    val voxFile = this.serialize()
    val stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    VoxWriter(stream).use { it.write(voxFile) }
}