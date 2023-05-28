package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.util.assertProjectFilesEqual
import hunternif.voxarch.editor.util.makeTestDir
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector3i
import org.junit.Assert.assertEquals
import org.junit.Test

class ImportProjectTest: BaseAppTest() {
    @Test
    fun `importing project without any data doesn't change project`() {
        val tempDir = makeTestDir("project")
        app.newBlueprint()
        app.createNode(Vector3i(0, 0, 0), Vector3i(0, 0, 0))
        app.buildVoxels()
        val refPath = tempDir.resolve("import_project_ref.voxarch")
        app.saveProjectFileAs(refPath)

        val importPath = resourcePath("project/2_rooms.voxarch")
        app.importProject(
            importPath,
            importBlueprints = false,
        )
        val testPath = tempDir.resolve("import_project_test.voxarch")
        app.saveProjectFileAs(testPath)

        assertProjectFilesEqual(refPath, testPath)
    }

    @Test
    fun `importing blueprints from project, undo redo`() {
        val bpNames = app.state.blueprintLibrary.blueprintsByName
        val bp = app.newBlueprint()
        app.renameBlueprint(bp, "main")
        assertEquals(setOf("main"), bpNames.keys)

        app.importProject(
            resourcePath("project/out_slot_test.voxarch"),
            importBlueprints = true,
        )
        assertEquals(3, app.state.blueprints.size)
        assertEquals(setOf("main", "main (1)", "all_walls"), bpNames.keys)
        assertEquals("main", bp.name)

        app.undo()
        assertEquals(1, app.state.blueprints.size)
        assertEquals(setOf("main"), bpNames.keys)

        app.redo()
        assertEquals(3, app.state.blueprints.size)
        assertEquals(setOf("main", "main (1)", "all_walls"), bpNames.keys)
        assertEquals("main", bp.name)
    }
}