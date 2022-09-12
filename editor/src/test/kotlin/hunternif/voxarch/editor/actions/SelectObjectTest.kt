package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.util.emptyArray3D
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SelectObjectTest : BaseActionTest() {
    private lateinit var node1: SceneNode
    private lateinit var node2: SceneNode
    private lateinit var voxels1: SceneVoxelGroup
    private lateinit var voxels2: SceneVoxelGroup

    @Before
    fun setup() = app.state.run {
        node1 = app.createRoom(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        node2 = app.createRoom(Vector3i(1, 2, 3), Vector3i(2, 2, 2))
        voxels1 = registry.newVoxelGroup("voxels1", emptyArray3D())
        voxels2 = registry.newVoxelGroup("voxels2", emptyArray3D())
        voxelRoot.addChild(voxels1)
        voxelRoot.addChild(voxels2)
    }

    @Test
    fun `test selectionBuilder undo redo`() = app.state.run {
        val builder = app.selectionBuilder()
        assertEquals(0, selectedObjects.size)
        builder.add(node1)
        assertEquals(setOf(node1), selectedObjects)
        builder.add(node2)
        assertEquals(setOf(node1, node2), selectedObjects)
        builder.remove(node1)
        assertEquals(setOf(node2), selectedObjects)
        builder.clear()
        assertEquals(0, selectedObjects.size)
        builder.add(node2)
        builder.commit()
        assertEquals(setOf(node2), selectedObjects)
        app.undo()
        assertEquals(0, selectedObjects.size)
        app.redo()
        assertEquals(setOf(node2), selectedObjects)
    }

    @Test
    fun `test selectObject undo redo`() = app.state.run {
        app.selectObject(node1)
        assertEquals(setOf(node1), selectedObjects)
        app.selectObject(node2)
        assertEquals(setOf(node1, node2), selectedObjects)
        app.undo()
        assertEquals(setOf(node1), selectedObjects)
        app.redo()
        assertEquals(setOf(node1, node2), selectedObjects)
    }

    @Test
    fun `test setSelectedObject undo redo`() = app.state.run {
        app.selectObject(node1)
        app.selectObject(node2)
        assertEquals(setOf(node1, node2), selectedObjects)
        app.setSelectedObject(node2)
        assertEquals(setOf(node2), selectedObjects)
        app.undo()
        assertEquals(setOf(node1, node2), selectedObjects)
        app.redo()
        assertEquals(setOf(node2), selectedObjects)
    }

    @Test
    fun `test unselectObject undo redo`() = app.state.run {
        app.selectObject(node1)
        app.selectObject(node2)
        assertEquals(setOf(node1, node2), selectedObjects)
        app.unselectObject(node2)
        assertEquals(setOf(node1), selectedObjects)
        app.undo()
        assertEquals(setOf(node1, node2), selectedObjects)
        app.redo()
        assertEquals(setOf(node1), selectedObjects)
    }

    @Test
    fun `test selectAll undo redo`() = app.state.run {
        assertEquals(0, selectedObjects.size)
        app.selectAll()
        assertEquals(setOf(node1, node2, voxels1, voxels2), selectedObjects)
        app.undo()
        assertEquals(0, selectedObjects.size)
        app.redo()
        assertEquals(setOf(node1, node2, voxels1, voxels2), selectedObjects)
    }

    @Test
    fun `test selectAll with mask`() = app.state.run {
        assertEquals(0, selectedObjects.size)
        app.selectAll(SelectMask.NODES)
        assertEquals(setOf(node1, node2), selectedObjects)
        app.undo()
        app.selectAll(SelectMask.VOXELS)
        assertEquals(setOf(voxels1, voxels2), selectedObjects)
    }

    @Test
    fun `test unselectAll undo redo`() = app.state.run {
        app.selectObject(node1)
        app.selectObject(node2)
        assertEquals(setOf(node1, node2), selectedObjects)
        app.unselectAll()
        assertEquals(0, selectedObjects.size)
        app.undo()
        assertEquals(setOf(node1, node2), selectedObjects)
        app.redo()
        assertEquals(0, selectedObjects.size)
    }

    @Test
    fun `test unselectAll with mask`() = app.state.run {
        app.selectAll()
        assertEquals(setOf(node1, node2, voxels1, voxels2), selectedObjects)
        app.unselectAll(SelectMask.NODES)
        assertEquals(setOf(voxels1, voxels2), selectedObjects)
        app.undo()
        app.unselectAll(SelectMask.VOXELS)
        assertEquals(setOf(node1, node2), selectedObjects)
    }

    @Test
    fun `test selectionBuilder unchanged selection is not saved`() = app.state.run {
        assertEquals(3, history.pastItems.size) // new project + created the 2 rooms
        app.selectObject(node1)
        assertEquals(setOf(node1), selectedObjects)
        assertEquals(4, history.pastItems.size)

        val builder = app.selectionBuilder()
        builder.add(node1)
        builder.commit()
        assertEquals(4, history.pastItems.size)

        val builder2 = app.selectionBuilder()
        builder2.remove(node1)
        builder2.add(node1)
        builder2.commit()
        assertEquals(4, history.pastItems.size)
    }

    @Test
    fun `test selectionBuilder with mask VOXELS`() = app.state.run {
        app.selectObject(node1)
        assertEquals(setOf(node1), selectedObjects)

        val builder = app.selectionBuilder(SelectMask.VOXELS)
        builder.clear()
        assertEquals(setOf(node1), selectedObjects)
        builder.remove(node1)
        assertEquals(setOf(node1), selectedObjects)
        builder.add(node2)
        assertEquals(setOf(node1), selectedObjects)
        builder.add(voxels1)
        assertEquals(setOf(voxels1, node1), selectedObjects)
        builder.add(voxels2)
        assertEquals(setOf(voxels1, voxels2, node1), selectedObjects)
    }

    @Test
    fun `test selectionBuilder with mask NODES`() = app.state.run {
        app.selectObject(voxels1)
        app.selectObject(node1)
        assertEquals(setOf(voxels1, node1), selectedObjects)

        val builder = app.selectionBuilder(SelectMask.NODES)
        builder.clear()
        assertEquals(setOf(voxels1), selectedObjects)
        builder.remove(voxels1)
        assertEquals(setOf(voxels1), selectedObjects)
        builder.add(voxels2)
        assertEquals(setOf(voxels1), selectedObjects)
        builder.add(node2)
        assertEquals(setOf(voxels1, node2), selectedObjects)
    }
}