package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.emptyArray3D
import org.junit.Assert.*
import org.junit.Test

class SceneRegistryTest {
    private val registry = SceneRegistry()

    @Test
    fun `register new objects in order`() {
        val root = registry.newObject()
        assertEquals(0, root.id)

        val node = registry.newNode(Node())
        assertEquals(1, node.id)

        val voxels = registry.newVoxelGroup("voxels", emptyArray3D())
        assertEquals(2, voxels.id)
        assertEquals(mapOf(0 to root, 1 to node, 2 to voxels), registry.objectIDs.map)

        val subset = registry.newSubset<SceneObject>("subset")
        assertEquals(0, subset.id)
        assertEquals(mapOf(0 to subset), registry.subsetIDs.map)
    }
}