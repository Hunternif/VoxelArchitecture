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
        assertEquals(emptyList<String>(), history.futureItems)

        history.append("first")
        // first item can't be removed
        assertEquals(false, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)

        history.moveBack()
        // first item can't be removed
        assertEquals(false, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)

        history.append("second")
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "second"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)

        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "second"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)

        history.moveBack()
        assertEquals(false, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)
        assertEquals(listOf("second"), history.futureItems)

        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "second"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)

        history.moveBack()
        history.moveBack()
        assertEquals(false, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)
        assertEquals(listOf("second"), history.futureItems)

        history.moveBack()
        assertEquals(false, history.hasPastItems())
        assertEquals(true, history.hasFutureItems())
        assertEquals(listOf("first"), history.pastItems)
        assertEquals(listOf( "second"), history.futureItems)

        history.append("third")
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "third"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)

        history.moveBack()
        history.moveBack()
        history.moveForward()
        history.moveForward()
        assertEquals(true, history.hasPastItems())
        assertEquals(false, history.hasFutureItems())
        assertEquals(listOf("first", "third"), history.pastItems)
        assertEquals(emptyList<String>(), history.futureItems)
    }
}