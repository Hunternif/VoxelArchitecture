package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.style.property.StyleSeed
import hunternif.voxarch.dom.style.property.StyleSize
import hunternif.voxarch.dom.style.property.inherit
import org.junit.Assert.*
import org.junit.Test

class ValueTest {
    @Test
    fun `value toString`() {
        assertEquals("exact", set("exact").toString())
        assertEquals("some value", value<String>("some value") { _, _ -> "exact" }.toString())
        assertEquals("random", random("a", "b").toString())
        assertEquals("inherit", StyleSize().inherit().toString())
        assertEquals("inherit", StyleSeed().inherit<Int>().toString())
    }
}