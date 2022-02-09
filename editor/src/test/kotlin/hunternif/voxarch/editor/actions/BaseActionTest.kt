package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import org.junit.Before

abstract class BaseActionTest {
    @Before
    fun resetState() {
        app.state.run {
            voxelRoot.children.clear()
            rootNode.children.clear()
            parentNode = rootNode
            sceneObjects.clear()
            selectedObjects.clear()
            hiddenObjects.clear()
            history.clear()
        }
    }

    protected val app: EditorAppImpl get() = Companion.app

    companion object {
        // Reusing a single app instance for all child tests
        // Assuming the window will get terminated automatically
        private val app by lazy { EditorAppImpl().also { it.init() } }
    }
}