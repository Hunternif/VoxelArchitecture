package hunternif.voxarch.editor.actions

import org.junit.Assert.*
import org.junit.Test

class HistoryTest {
    @Test
    fun `move through history`() {
        val history = History<String>()
        assertEquals(false, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(emptyList<String>(), history.pastItems)

        history.append("first")
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)

        history.append("second")
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "second"), history.pastItems)

        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "second"), history.pastItems)

        history.moveBack()
        assertEquals(true, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)

        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "second"), history.pastItems)

        history.moveBack()
        history.moveBack()
        assertEquals(false, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(emptyList<String>(), history.pastItems)

        history.moveBack()
        assertEquals(false, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(emptyList<String>(), history.pastItems)

        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)

        history.append("third")
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "third"), history.pastItems)

        history.moveBack()
        history.moveBack()
        history.moveForward()
        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "third"), history.pastItems)
    }
}