package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.plan.Room
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
        val selC = selectChildOf("base")
        val sum = selA + selB + selC
        assertNotEquals(selA, sum)
        assertNotEquals(selB, sum)
        assertNotEquals(selC, sum)
        assertEquals(setOf("abc", "123"), sum.styleClasses)
        assertEquals(setOf(Int::class.java), sum.types)
        assertEquals(setOf(domBld), sum.instances)
        assertEquals(1, sum.parentSelectors.size)
        assertEquals(setOf("base"), sum.parentSelectors.first().styleClasses)
    }

    @Test
    fun `selector toString`() {
        val domBld = domRoot()
        val selA = select("abc").type(Room::class.java)
        val selB = select("123").instance(domBld)
        val selC = selectChildOf("base", "test")
        val selD = selectDescendantOf("base", "test")

        assertEquals("Room .abc", selA.toString())
        assertEquals(".123 #DomRoot", selB.toString())
        assertEquals(".base .test >", selC.toString())
        assertEquals("[.base .test]", selD.toString())
        assertEquals(".base .test > Room .abc .123 #DomRoot", (selA + selB + selC).toString())
        assertEquals("[.base .test] Room .abc .123 #DomRoot", (selA + selB + selD).toString())
    }
}