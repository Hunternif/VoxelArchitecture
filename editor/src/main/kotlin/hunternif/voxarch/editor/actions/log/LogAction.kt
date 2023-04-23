package hunternif.voxarch.editor.actions.log

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.AppAction

class LogAction(val msg: LogMessage) : AppAction {
    override fun invoke(app: EditorAppImpl) {
        app.logs.add(msg)
    }
}