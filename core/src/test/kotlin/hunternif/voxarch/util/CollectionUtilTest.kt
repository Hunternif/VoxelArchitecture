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

    @Test
    fun `get or insert into list`() {
        val list = mutableListOf<String?>()

        val foo = list.getOrInsert(0) { "foo" }
        assertEquals("foo", foo)
        assertEquals(1, list.size)
        assertEquals("foo", list[0])

        val bar = list.getOrInsert(2) { "bar" }
        assertEquals("bar", bar)
        assertEquals(3, list.size)
        assertEquals("foo", list[0])
        assertEquals(null, list[1])
        assertEquals("bar", list[2])

        val buz = list.getOrInsert(0) { "buz" }
        assertEquals("foo", buz)

        val lol = list.getOrInsert(1) { "lol" }
        assertEquals("lol", lol)
        assertEquals("lol", list[1])
    }
}