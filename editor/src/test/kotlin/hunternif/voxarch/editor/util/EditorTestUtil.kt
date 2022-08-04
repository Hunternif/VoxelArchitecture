package hunternif.voxarch.editor.util

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import org.apache.commons.io.IOUtils
import org.junit.Assert
import java.nio.file.Files
import java.nio.file.Paths

/** Asserts all properties except children. */
fun assertNodeEquals(
    expected: Node,
    actual: Node
) {
    Assert.assertEquals(expected::class, actual::class)
    Assert.assertEquals(expected.origin, actual.origin)
    Assert.assertEquals(expected.type, actual.type)
    Assert.assertEquals(expected.rotationY, actual.rotationY, 0.0)
    Assert.assertEquals(expected.size, actual.size)
    Assert.assertEquals(expected.width, actual.width, 0.0)
    Assert.assertEquals(expected.height, actual.height, 0.0)
    Assert.assertEquals(expected.length, actual.length, 0.0)
    when (expected) {
        is PolygonRoom -> {
            Assert.assertEquals(expected.shape, (actual as PolygonRoom).shape)
            Assert.assertEquals(expected.isCentered(), actual.isCentered())
            Assert.assertEquals(expected.polygon, actual.polygon)
        }
        is Room -> {
            Assert.assertEquals(expected.isCentered(), (actual as Room).isCentered())
        }
        is Path -> {
            Assert.assertEquals(expected.points, (actual as Path).points)
        }
    }
}

fun assertFilesEqual(
    expected: java.nio.file.Path,
    actual: java.nio.file.Path
) {
    val expectedReader = Files.newBufferedReader(expected)
    val actualReader = Files.newBufferedReader(actual)
    expectedReader.use {
        actualReader.use {
            Assert.assertTrue(IOUtils.contentEquals(expectedReader, actualReader))
        }
    }
}

fun makeTestDir(name: String): java.nio.file.Path {
    val path = Paths.get("./out/$name")
    if (!Files.exists(path)) {
        Files.createDirectories(path)
    }
    return path
}