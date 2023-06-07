package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.buildVoxels
import hunternif.voxarch.editor.actions.exportVoxFile
import hunternif.voxarch.editor.actions.generateNodes
import hunternif.voxarch.editor.actions.openProjectFile
import hunternif.voxarch.editor.file.serializeToXmlStr
import hunternif.voxarch.editor.util.assertFilesEqual
import hunternif.voxarch.editor.util.resourcePath
import org.junit.Assert.assertEquals
import org.junit.Test

class DomGableRoofDecorTest : BaseAppTest() {
    // Opens a test project file, executes the blueprint, verifies node tree and voxels.
    @Test
    fun `run blueprint with DomRepeat`() {
        val refProjectPath = resourcePath("project/gable_roof_test.voxarch")
        app.openProjectFile(refProjectPath)
        val refNodeTree = serializeToXmlStr(app.state.rootNode.node, true)

        // Test nodes
        app.generateNodes()
        val generatedNodeTree = serializeToXmlStr(app.state.rootNode.node, true)
        assertEquals(refNodeTree, generatedNodeTree)

        // Export voxels
        val refVoxelPath = tempDir.resolve("gable_roof_ref.vox")
        app.exportVoxFile(refVoxelPath)

        app.buildVoxels()

        val testVoxelPath = tempDir.resolve("gable_roof_exported.vox")
        app.exportVoxFile(testVoxelPath)

        assertFilesEqual(refVoxelPath, testVoxelPath)
    }
}