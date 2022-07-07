package hunternif.voxarch.util

import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.vector.Array3D
import org.junit.Assert.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun today(): String =
    DateTimeFormatter.ofPattern("YYY-MM-dd_HH_mm_ss")
        .format(LocalDateTime.now())

/**
 * Helper for unit tests, prints a horizontal slice of a 3D array as a string, e.g.:
 * """
 * ABCD
 * EFGH
 * """
 */
fun <T> Array3D<T>.printSliceY(y: Int): String {
    val sb = StringBuilder()
    for (z in minZ..maxZ) {
        if (z > 0) sb.append("\n")
        for (x in minX..maxX) {
            sb.append(get(x, y, z).toString())
        }
    }
    return sb.toString()
}

fun <T> assertStorageEquals(
    expected: IArray3D<in T>,
    actual: IArray3D<in T>
) {
    assertEquals(expected.width, actual.width)
    assertEquals(expected.height, actual.height)
    assertEquals(expected.length, actual.length)
    expected.forEachPos { x, y, z, t ->
        assertEquals(t, actual[x, y, z])
    }
}