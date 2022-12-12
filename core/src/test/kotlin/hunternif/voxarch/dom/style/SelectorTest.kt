package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import org.junit.Assert.*
import org.junit.Test

class SelectorTest {
    @Test
    fun `empty selector`() {
        val selector = Selector()
        assertTrue(selector.isEmpty())
        selector.style("style")
        assertFalse(selector.isEmpty())
    }

    @Test
    fun `add selectors`() {
        val domBld = domRoot()
        val selA = select("abc").type(Int::class.java)
        val selB = select("123").instance(domBld)
        val selC = selectInherit("base")
        val sum = selA + selB + selC
        assertNotEquals(selA, sum)
        assertNotEquals(selB, sum)
        assertNotEquals(selC, sum)
        assertEquals(setOf("abc", "123"), sum.styleClasses)
        assertEquals(setOf("base"), sum.inheritedStyleClasses)
        assertEquals(setOf(Int::class.java), sum.types)
        assertEquals(setOf(domBld), sum.instances)
    }
}