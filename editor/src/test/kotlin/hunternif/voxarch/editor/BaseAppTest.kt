package hunternif.voxarch.editor

import hunternif.voxarch.editor.actions.newProject
import org.junit.Before

abstract class BaseAppTest {
    @Before
    fun resetState() {
        app.newProject()
        app.state.catchExceptions = false
    }

    protected val app: EditorAppImpl get() = Companion.app

    companion object {
        // Reusing a single app instance for all child tests
        // Assuming the window will get terminated automatically
        private val app by lazy { EditorAppImpl().also { it.init() } }
    }
}