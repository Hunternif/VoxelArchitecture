package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f
import org.joml.Vector3i
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MoveObjectsTest : BaseActionTest() {
    private lateinit var node: SceneNode
    private lateinit var voxels: SceneVoxelGroup

    @Before
    fun setup() = app.state.run {
        node = app.createRoom(Vector3i(10, 20, 30), Vector3i(11, 21, 31))
        voxels = SceneVoxelGroup("voxels", emptyArray3D())
        voxels.origin.set(100f, 200f, 300f)
        sceneObjects.add(voxels)
        voxelRoot.addChild(voxels)
    }

    @Test
    fun `move nodes & voxels, undo redo`() {
        val builder = app.moveBuilder(listOf(node, voxels))
        assertEquals(Vec3(10, 20, 30), node.node.origin)
        assertEquals(Vec3(100, 200, 300), voxels.origin.toVec3())

        builder.setMove(Vector3f(20f, -5f, -6f))
        assertEquals(Vec3(30, 15, 24), node.node.origin)
        assertEquals(Vec3(120, 195, 294), voxels.origin.toVec3())

        builder.setMove(Vector3f(10f, 10f, 10f))
        assertEquals(Vec3(20, 30, 40), node.node.origin)
        assertEquals(Vec3(110, 210, 310), voxels.origin.toVec3())

        builder.commit()
        assertEquals(Vec3(20, 30, 40), node.node.origin)
        assertEquals(Vec3(110, 210, 310), voxels.origin.toVec3())

        app.undo()
        assertEquals(Vec3(10, 20, 30), node.node.origin)
        assertEquals(Vec3(100, 200, 300), voxels.origin.toVec3())

        app.redo()
        assertEquals(Vec3(20, 30, 40), node.node.origin)
        assertEquals(Vec3(110, 210, 310), voxels.origin.toVec3())
    }
}