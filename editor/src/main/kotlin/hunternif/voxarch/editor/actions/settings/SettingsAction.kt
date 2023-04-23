package hunternif.voxarch.editor.actions.settings

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Settings
import hunternif.voxarch.editor.actions.AppAction

class SettingsAction(
    private val newSettings: Settings,
) : AppAction {
    override fun invoke(app: EditorAppImpl) {
        app.state.settings = newSettings
    }
}
