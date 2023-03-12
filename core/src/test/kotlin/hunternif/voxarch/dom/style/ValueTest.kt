package hunternif.voxarch.dom.style

import org.junit.Assert.*
import org.junit.Test

class ValueTest {
    @Test
    fun `value toString`() {
        assertEquals("exact", set("exact").toString())
        assertEquals("some value", value<String>("some value") { _, _ -> "exact" }.toString())
        assertEquals("random", random("a", "b").toString())
        assertEquals("inherit", inherit<Double>().toString())
        assertEquals("inherit", inherit<Int>().toString())
    }
}