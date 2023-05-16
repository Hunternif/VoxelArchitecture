package hunternif.voxarch.editor.builder

import hunternif.voxarch.plan.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuilderLibraryTest {
    @Test
    fun `find builders for node type`() {
        val library = BuilderLibrary()

        val nodeEntries = library.findBuildersFor(Node())
        assertContainsBuilder<Node>(nodeEntries)
        assertNotContainsBuilder<Path>(nodeEntries)

        val wallEntries = library.findBuildersFor(Wall())
        assertContainsBuilder<Node>(wallEntries)
        assertNotContainsBuilder<Path>(wallEntries)

        val pathEntries = library.findBuildersFor(Path())
        assertContainsBuilder<Node>(pathEntries)
        assertContainsBuilder<Path>(pathEntries)
    }

    @Test
    fun `all builders have unique names`() {
        val library = BuilderLibrary()
        val nameSet = library.buildersByName.keys
        assertEquals(nameSet.size, library.allBuilders.size)
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