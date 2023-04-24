package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.history.StackingAction
import org.junit.Assert.*
import org.junit.Test

class StackingActionTest : BaseAppTest() {
    private var count = 0

    @Test
    fun `stack 2 actions together, undo redo`() {
        assertEquals(0, count)
        val pastItems = app.state.history.pastItems.toList()

        val act1 = Stacking(this)
        app.historyAction(act1)
        assertEquals(1, count)
        assertEquals(pastItems + act1, app.state.history.pastItems)

        app.undo()
        assertEquals(0, count)
        assertEquals(pastItems, app.state.history.pastItems)

        app.redo()
        assertEquals(1, count)
        assertEquals(pastItems + act1, app.state.history.pastItems)

        // Add 2nd action
        app.historyAction(Stacking(this))
        assertEquals(2, count)
        // still the same action in history
        assertEquals(pastItems + act1, app.state.history.pastItems)

        app.undo()
        assertEquals(0, count)
        assertEquals(pastItems, app.state.history.pastItems)

        app.redo()
        assertEquals(2, count)
        assertEquals(pastItems + act1, app.state.history.pastItems)
    }

    private class Stacking(
        private val data: StackingActionTest,
    ) : HistoryAction(
        "stacking test", ""
    ), StackingAction<Stacking> {
        private var increment = 1
        override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
            data.count += increment
        }

        override fun revert(app: EditorAppImpl) {
            data.count -= increment
        }

        override fun update(nextAction: Stacking) {
            this.increment += nextAction.increment
        }
    }
}