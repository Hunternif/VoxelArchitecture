package hunternif.voxarch.util

import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.IStorage3D
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
    expected: IStorage3D<in T>,
    actual: IStorage3D<in T>
) {
    assertEquals(expected.width, actual.width)
    assertEquals(expected.height, actual.height)
    assertEquals(expected.length, actual.length)
    assertEquals(expected.minX, actual.minX)
    assertEquals(expected.minY, actual.minY)
    assertEquals(expected.minZ, actual.minZ)
    assertEquals(expected.maxX, actual.maxX)
    assertEquals(expected.maxY, actual.maxY)
    assertEquals(expected.maxZ, actual.maxZ)
    expected.forEachPos { x, y, z, t ->
        assertEquals(t, actual[x, y, z])
    }
}

/** Asserts all properties except children. */
fun assertNodeEquals(
    expected: Node,
    actual: Node,
    testTags: Boolean = true,
) {
    assertEquals(expected::class, actual::class)
    assertEquals(expected.origin, actual.origin)
    if (testTags) assertEquals(expected.tags, actual.tags)
    assertEquals(expected.rotationY, actual.rotationY, 0.0)
    assertEquals(expected.size, actual.size)
    assertEquals(expected.width, actual.width, 0.0)
    assertEquals(expected.height, actual.height, 0.0)
    assertEquals(expected.length, actual.length, 0.0)
    when (expected) {
        is PolyRoom -> {
            assertEquals(expected.shape, (actual as PolyRoom).shape)
            assertEquals(expected.isCentered(), actual.isCentered())
            assertNodeEquals(expected.polygon, actual.polygon)
        }
        is Room -> {
            assertEquals(expected.isCentered(), (actual as Room).isCentered())
        }
        is Path -> {
            assertEquals(expected.points, (actual as Path).points)
        }
    }
}

/** Asserts all properties are equal, including all children recursively. */
fun assertNodeTreeEqualsRecursive(
    expected: Node,
    actual: Node,
    testTags: Boolean = true,
) {
    val expectedTraversal = expected.query<Node>().toList()
    val actualTraversal = actual.query<Node>().toList()
    assertEquals(expectedTraversal.size, actualTraversal.size)
    expectedTraversal.zip(actualTraversal).forEach { (exp, act) ->
        assertNodeEquals(exp, act, testTags)
    }
}