package hunternif.voxarch.editor.util

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.nio.file.Files
import java.nio.file.Paths

/** Asserts all properties except children. */
fun assertNodeEquals(
    expected: Node,
    actual: Node
) {
    assertEquals(expected::class, actual::class)
    assertEquals(expected.origin, actual.origin)
    assertEquals(expected.type, actual.type)
    assertEquals(expected.rotationY, actual.rotationY, 0.0)
    assertEquals(expected.size, actual.size)
    assertEquals(expected.width, actual.width, 0.0)
    assertEquals(expected.height, actual.height, 0.0)
    assertEquals(expected.length, actual.length, 0.0)
    when (expected) {
        is PolygonRoom -> {
            assertEquals(expected.shape, (actual as PolygonRoom).shape)
            assertEquals(expected.isCentered(), actual.isCentered())
            assertEquals(expected.polygon, actual.polygon)
        }
        is Room -> {
            assertEquals(expected.isCentered(), (actual as Room).isCentered())
        }
        is Path -> {
            assertEquals(expected.points, (actual as Path).points)
        }
    }
}

fun assertFilesEqual(
    expected: java.nio.file.Path,
    actual: java.nio.file.Path
) {
    val expectedStream = Files.newInputStream(expected)
    val actualStream = Files.newInputStream(actual)
    expectedStream.use {
        actualStream.use {
            assertTrue(IOUtils.contentEquals(expectedStream, actualStream))
        }
    }
}

fun assertTextFilesEqual(
    expected: java.nio.file.Path,
    actual: java.nio.file.Path
) {
    val expectedText = Files.newBufferedReader(expected).use { it.readText() }
    val actualText = Files.newBufferedReader(actual).use { it.readText() }
    assertEquals(expectedText, actualText)
}

fun makeTestDir(name: String): java.nio.file.Path {
    val path = Paths.get("./out/$name")
    if (!Files.exists(path)) {
        Files.createDirectories(path)
    }
    return path
}