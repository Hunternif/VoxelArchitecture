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
    actual: Node,
    testTags: Boolean = true,
) {
    assertEquals("class", expected::class, actual::class)
    assertEquals("origin", expected.origin, actual.origin)
    assertEquals("start", expected.start, actual.start)
    if (testTags) assertEquals("tags", expected.tags, actual.tags)
    assertEquals("rotationY", expected.rotationY, actual.rotationY, 0.0)
    assertEquals("size", expected.size, actual.size)
    assertEquals("width", expected.width, actual.width, 0.0)
    assertEquals("height", expected.height, actual.height, 0.0)
    assertEquals("depth", expected.depth, actual.depth, 0.0)
    assertEquals("builder", expected.builder, actual.builder)
    when (expected) {
        is PolyRoom -> {
            assertEquals("shape", expected.shape, (actual as PolyRoom).shape)
            assertNodeEquals(expected.polygon, actual.polygon)
        }
        is Path -> {
            assertEquals("points", expected.points, (actual as Path).points)
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