package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl

/**
 * Some actions have multiple steps reflected in the UI, but only a single
 * operation that's written to history.
 * E.g. when moving an object in the scene, it's temporary position is instantly
 * visible, but only the final position will be written to history.
 *
 * This builder class exposes methods that visualize intermediate steps.
 * Finally, [build] creates a complete action that can be written to history.
 */
abstract class HistoryActionBuilder(protected val app: EditorAppImpl) {
    /** Creates the action instance that will be written to history. */
    protected abstract fun build(): HistoryAction
    /** Builds the action and writes it to history without executing,
     * because all intermediate steps have been executed. */
    open fun commit() {
        app.state.history.append(build())
    }
}