package hunternif.voxarch.magicavoxel

import com.scs.voxlib.VoxReader
import hunternif.voxarch.vector.Array3D
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path

fun readVoxFile(
    path: Path,
    useModelOffset: Boolean = true,
): VoxFileStorage {
    val voxFile = VoxReader(byteStreamFromFile(path)).use { it.read() }
    return VoxFileStorage.fromFile(voxFile, useModelOffset)
}

inline fun <reified C: Any> readVoxFile(
    path: Path,
    colorMap: (VoxColor?) -> C
): Array3D<C> {
    val voxFile = VoxReader(byteStreamFromFile(path)).use { it.read() }
    val storage = VoxFileStorage.fromFile(voxFile)
    return Array3D(storage.width, storage.height, storage.depth) { x, y, z ->
        colorMap(storage[x, y, z])
    }
}

fun byteStreamFromFile(path: Path) =
    ByteArrayInputStream(
        Files.newInputStream(path).use { it.readBytes() }
    )