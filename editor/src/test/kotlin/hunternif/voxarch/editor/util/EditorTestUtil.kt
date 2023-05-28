package hunternif.voxarch.editor.util

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.*
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList
import java.util.stream.Collectors
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

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
    assertEquals(expected.treeToString(testTags), actual.treeToString(testTags))
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

fun assertProjectFilesEqual(
    expected: java.nio.file.Path,
    actual: java.nio.file.Path
) {
    val zipfsRef = newZipFileSystem(expected)
    val zipfsTest = newZipFileSystem(actual)
    zipfsRef.use {
        zipfsTest.use {
            val refFiles = Files.walk(zipfsRef.getPath("/"), 2)
                .filter { it.isRegularFile() }
                .collect(Collectors.toList())
            val testFiles = Files.walk(zipfsTest.getPath("/"), 2)
                .filter { it.isRegularFile() }
                .collect(Collectors.toList())
            assertEquals(
                refFiles.sorted().joinToString("\n"),
                testFiles.sorted().joinToString("\n"),
            )

            refFiles.forEach {
                val testFilePath = zipfsTest.getPath(it.toString())
                if (it.extension in setOf("yaml", "json", "xml", "vcss")) {
                    assertTextFilesEqual(it, testFilePath)
                } else {
                    assertFilesEqual(it, testFilePath)
                }
            }
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