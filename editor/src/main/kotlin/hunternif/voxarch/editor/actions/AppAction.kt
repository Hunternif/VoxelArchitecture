package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl

interface AppAction : Event {
    fun invoke(app: EditorAppImpl)
}