package hunternif.voxarch.editor.actions.history

import hunternif.voxarch.editor.actions.AppAction

/**
 * Actions that sticks together with similar actions, so that there is only
 * 1 entry in history.
 * This is typically used for typing actions, which are updated frequently.
 */
interface StackingAction<A : StackingAction<A>> : AppAction {
    /**
     * Combine this old action with the next consecutive action.
     */
    fun update(nextAction: A)

    /**
     * Combine this old action with the next consecutive action,
     * if the type is right.
     */
    fun maybeUpdate(nextAction: AppAction) {
        if (stacksWith(nextAction)) {
            @Suppress("UNCHECKED_CAST")
            update(nextAction as A)
        }
    }

    fun stacksWith(action: AppAction): Boolean =
        this::class.java == action::class.java
}