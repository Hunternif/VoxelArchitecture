package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f
import org.joml.Vector3i
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MoveObjectsTest : BaseAppTest() {
    private lateinit var node: SceneNode
    private lateinit var voxels: SceneVoxelGroup

    @Before
    fun setup() = app.state.run {
        node = app.createRoom(Vector3i(10, 20, 30), Vector3i(11, 21, 31))
        voxels = registry.newVoxelGroup("voxels", emptyArray3D())
        voxels.position.set(100f, 200f, 300f)
        voxelRoot.addChild(voxels)
    }

    @Test
    fun `move nodes & voxels, undo redo`() {
        val builder = app.moveBuilder(listOf(node, voxels))
        assertEquals(Vec3(10, 20, 30), node.node.position)
        assertEquals(Vec3(100, 200, 300), voxels.position)

        builder.setMove(Vector3f(20f, -5f, -6f))
        assertEquals(Vec3(30, 15, 24), node.node.position)
        assertEquals(Vec3(120, 195, 294), voxels.position)

        builder.setMove(Vector3f(10f, 10f, 10f))
        assertEquals(Vec3(20, 30, 40), node.node.position)
        assertEquals(Vec3(110, 210, 310), voxels.position)

        builder.commit()
        assertEquals(Vec3(20, 30, 40), node.node.position)
        assertEquals(Vec3(110, 210, 310), voxels.position)

        app.undo()
        assertEquals(Vec3(10, 20, 30), node.node.position)
        assertEquals(Vec3(100, 200, 300), voxels.position)

        app.redo()
        assertEquals(Vec3(20, 30, 40), node.node.position)
        assertEquals(Vec3(110, 210, 310), voxels.position)
    }
}