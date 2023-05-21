package hunternif.voxarch.util

import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.Vec3
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

fun assertVec3Equals(expected: Vec3, actual: Vec3, delta: Double = 0.0000001) {
    assertEquals("x", expected.x, actual.x, delta)
    assertEquals("y", expected.y, actual.y, delta)
    assertEquals("z", expected.z, actual.z, delta)
}

fun <T> assertStorageEquals(
    expected: IStorage3D<in T>,
    actual: IStorage3D<in T>
) {
    assertEquals("width", expected.width, actual.width)
    assertEquals("height", expected.height, actual.height)
    assertEquals("depth", expected.depth, actual.depth)
    assertEquals("minX", expected.minX, actual.minX)
    assertEquals("minY", expected.minY, actual.minY)
    assertEquals("minZ", expected.minZ, actual.minZ)
    assertEquals("maxX", expected.maxX, actual.maxX)
    assertEquals("maxY", expected.maxY, actual.maxY)
    assertEquals("maxZ", expected.maxZ, actual.maxZ)
    expected.forEachPos { x, y, z, t ->
        assertEquals("at ($x, $y, $z)", t, actual[x, y, z])
    }
}

/** Asserts all properties except children. */
fun assertNodeEquals(
    expected: Node,
    actual: Node,
    testTags: Boolean = true,
) {
    assertEquals("$expected class", expected::class, actual::class)
    assertEquals("$expected origin", expected.origin, actual.origin)
    assertEquals("$expected start", expected.start, actual.start)
    if (testTags) assertEquals("$expected tags", expected.tags, actual.tags)
    assertEquals("$expected rotationY", expected.rotationY, actual.rotationY, 0.0)
    assertEquals("$expected size", expected.size, actual.size)
    assertEquals("$expected width", expected.width, actual.width, 0.0)
    assertEquals("$expected height", expected.height, actual.height, 0.0)
    assertEquals("$expected depth", expected.depth, actual.depth, 0.0)
    assertEquals("$expected builder", expected.builder, actual.builder)
    assertEquals("$expected children count", expected.children.size, actual.children.size)
    when (expected) {
        is PolyRoom -> {
            assertEquals("$expected shape", expected.shape, (actual as PolyRoom).shape)
            assertNodeEquals(expected.polygon, actual.polygon)
        }
        is Path -> {
            assertEquals("$expected points", expected.points, (actual as Path).points)
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
    assertEquals("node count", expectedTraversal.size, actualTraversal.size)
    expectedTraversal.zip(actualTraversal).forEach { (exp, act) ->
        assertNodeEquals(exp, act, testTags)
    }
}