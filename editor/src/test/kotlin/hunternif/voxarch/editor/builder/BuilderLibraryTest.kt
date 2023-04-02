package hunternif.voxarch.editor.builder

import hunternif.voxarch.plan.*
import org.junit.Assert.assertTrue
import org.junit.Test

class BuilderLibraryTest {
    @Test
    fun `find builders for node type`() {
        val library = BuilderLibrary()

        val nodeEntries = library.findBuildersFor(Node())
        assertContainsBuilder<Node>(nodeEntries)
        assertNotContainsBuilder<Room>(nodeEntries)
        assertNotContainsBuilder<Path>(nodeEntries)

        val roomEntries = library.findBuildersFor(Room())
        assertContainsBuilder<Node>(roomEntries)
        assertContainsBuilder<Room>(roomEntries)
        assertNotContainsBuilder<Path>(roomEntries)

        val wallEntries = library.findBuildersFor(Wall())
        assertContainsBuilder<Node>(wallEntries)
        assertNotContainsBuilder<Room>(wallEntries)
        assertNotContainsBuilder<Path>(wallEntries)

        val pathEntries = library.findBuildersFor(Path())
        assertContainsBuilder<Node>(pathEntries)
        assertNotContainsBuilder<Room>(pathEntries)
        assertContainsBuilder<Path>(pathEntries)
    }

    private inline fun <reified N: Node> assertContainsBuilder(
        entries: Collection<BuilderLibrary.Entry>,
    ) {
        assertTrue(
            "contains builders of class ${N::class.java.simpleName}",
            entries.any { it.builder.nodeClass == N::class.java }
        )
    }

    private inline fun <reified N: Node> assertNotContainsBuilder(
        entries: Collection<BuilderLibrary.Entry>,
    ) {
        assertTrue(
            "does not contain builders of class ${N::class.java.simpleName}",
            entries.none { it.builder.nodeClass == N::class.java }
        )
    }
}