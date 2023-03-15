package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BuildVoxelsTest : BaseAppTest() {
    @Test
    fun `build voxels twice, undo redo`() = app.state.run {
        assertEquals(0, sceneObjects.size)
        assertEquals(0, voxelRoot.children.size)
        app.buildVoxels()
        assertEquals(1, sceneObjects.size)
        assertEquals(1, voxelRoot.children.size)
        app.buildVoxels()
        assertEquals(1, sceneObjects.size)
        assertEquals(1, voxelRoot.children.size)
        app.undo()
        assertEquals(1, sceneObjects.size)
        assertEquals(1, voxelRoot.children.size)
        app.redo()
        assertEquals(1, sceneObjects.size)
        assertEquals(1, voxelRoot.children.size)
        app.undo()
        app.undo()
        assertEquals(0, sceneObjects.size)
        assertEquals(0, voxelRoot.children.size)
        app.redo()
        assertEquals(1, sceneObjects.size)
        assertEquals(1, voxelRoot.children.size)
    }
}