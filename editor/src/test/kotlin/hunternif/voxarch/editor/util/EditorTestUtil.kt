package hunternif.voxarch.editor.util

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.*
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList

/** Asserts all properties except children. */
fun assertNodeEquals(
    expected: Node,
    actual: Node
) {
    assertEquals(expected::class, actual::class)
    assertEquals(expected.origin, actual.origin)
    assertEquals(expected.start, actual.start)
    assertEquals(expected.tags, actual.tags)
    assertEquals(expected.rotationY, actual.rotationY, 0.0)
    assertEquals(expected.size, actual.size)
    assertEquals(expected.width, actual.width, 0.0)
    assertEquals(expected.height, actual.height, 0.0)
    assertEquals(expected.depth, actual.depth, 0.0)
    when (expected) {
        is PolyRoom -> {
            assertEquals(expected.shape, (actual as PolyRoom).shape)
            assertEquals(expected.polygon, actual.polygon)
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

/** Asserts DOM structures are equal, including all children recursively,
 * but excluding variable properties like visibility and style classes. */
fun assertDomTreeStructureEqualsRecursive(
    expected: DomBuilder,
    actual: DomBuilder,
) {
    val queue = LinkedList<Pair<DomBuilder, DomBuilder>>()
    queue.push(expected to actual)
    while (queue.isNotEmpty()) {
        val (exp, act) = queue.pop()
        assertEquals(exp::class, act::class)
        assertEquals(exp.seedOffset, act.seedOffset)
        assertEquals(exp.children.size, act.children.size)
        exp.children.zip(act.children).forEach { queue.push(it) }
    }
}