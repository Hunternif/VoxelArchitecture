package hunternif.voxarch.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CollectionUtilTest {
    @Test
    fun `loop sequence`() {
        val first10 = listOf(1, 2, 3).loopSequence().take(10).toList()
        assertEquals(listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, 1), first10)
    }

    @Test(expected = NoSuchElementException::class)
    fun `loop empty sequence`() {
        emptyList<Int>().loopSequence().toList()
    }
}