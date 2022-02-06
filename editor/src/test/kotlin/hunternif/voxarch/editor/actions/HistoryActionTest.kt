package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.util.resourcePath
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.lwjgl.glfw.GLFW

class HistoryActionTest {
    @Test
    fun `test ImportVoxFile`() {
        app.importVoxFile(resourcePath("vox/voxarch-wfc-10x10x10-2021-12-05_19_16_49.vox"))
        assertEquals(1, app.state.voxelRoot.children.size)
        app.undo()
        assertEquals(0, app.state.voxelRoot.children.size)
    }

    companion object {
        val app = EditorAppImpl()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            app.init()
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            GLFW.glfwTerminate()
        }
    }
}