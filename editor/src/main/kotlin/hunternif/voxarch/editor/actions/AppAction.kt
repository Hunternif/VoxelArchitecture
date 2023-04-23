package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl

interface AppAction {
    fun invoke(app: EditorAppImpl)
}