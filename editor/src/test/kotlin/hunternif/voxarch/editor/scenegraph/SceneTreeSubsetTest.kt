package hunternif.voxarch.editor.scenegraph

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SceneTreeSubsetTest {
    private lateinit var tree: SceneTree
    private lateinit var subset1: SceneTree.Subset
    private lateinit var subset2: SceneTree.Subset
    private lateinit var parent: SceneObject
    private lateinit var child: SceneObject

    @Before
    fun setup() {
        tree = SceneTree()
        subset1 = SceneTree.Subset("parent subset")
        subset2 = SceneTree.Subset("child subset")
        tree.subsets.add(subset1)
        tree.subsets.add(subset2)

        parent = SceneObject()
        child = SceneObject()
        tree.root.attach(parent)
        parent.attach(child)
    }

    @Test
    fun `detach and reattach maintains subsets`() {
        subset1.add(parent)
        subset2.add(child)
        assertEquals(setOf(parent), subset1.toSet())
        assertEquals(setOf(child), subset2.toSet())
        assertEquals(setOf(parent, child), tree.items.toSet())

        val detachedChild = child.detach()
        assertEquals(setOf(parent), subset1.toSet())
        assertEquals(emptySet<SceneTree>(), subset2.toSet())
        assertEquals(setOf(parent), tree.items.toSet())

        detachedChild.reattach()
        assertEquals(setOf(parent), subset1.toSet())
        assertEquals(setOf(child), subset2.toSet())
        assertEquals(setOf(parent, child), tree.items.toSet())
    }

    @Test
    fun `detach and reattach maintains nested subsets`() {
        subset1.add(parent)
        subset2.add(child)
        assertEquals(setOf(parent), subset1.toSet())
        assertEquals(setOf(child), subset2.toSet())
        assertEquals(setOf(parent, child), tree.items.toSet())

        val detachedParent = parent.detach()
        assertEquals(emptySet<SceneTree>(), subset1.toSet())
        assertEquals(emptySet<SceneTree>(), subset2.toSet())
        assertEquals(emptySet<SceneTree>(), tree.items.toSet())

        detachedParent.reattach()
        assertEquals(setOf(parent), subset1.toSet())
        assertEquals(setOf(child), subset2.toSet())
        assertEquals(setOf(parent, child), tree.items.toSet())
    }
}