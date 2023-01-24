package hunternif.voxarch.magicavoxel

import com.scs.voxlib.VoxReader
import hunternif.voxarch.vector.Array3D
import java.nio.file.Files
import java.nio.file.Path

fun readVoxFile(path: Path): VoxFileStorage {
    val voxFile = VoxReader(Files.newInputStream(path)).use { it.read() }
    return VoxFileStorage.fromFile(voxFile)
}

inline fun <reified C: Any> readVoxFile(
    path: Path,
    colorMap: (VoxColor?) -> C
): Array3D<C> {
    val voxFile = VoxReader(Files.newInputStream(path)).use { it.read() }
    val storage = VoxFileStorage.fromFile(voxFile)
    return Array3D(storage.width, storage.height, storage.depth) { x, y, z ->
        colorMap(storage[x, y, z])
    }
}