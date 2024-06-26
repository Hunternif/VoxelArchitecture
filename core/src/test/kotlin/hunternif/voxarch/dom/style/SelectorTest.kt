package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuildStats
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.plan.Node
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
    fun `invalid selector`() {
        val ctx = DomBuildContext(Node(), Stylesheet(), 0, DomBuildStats())
        val element = StyledElement(DomBuilder(), ctx)

        val selector = Selector()
        assertTrue(selector.appliesTo(element))

        selector.isInvalid = true
        assertFalse(selector.appliesTo(element))
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
        val selE = selectDescendantOf("extra")

        assertEquals("*", Selector().toString())
        assertEquals("Room .abc", selA.toString())
        assertEquals(".123 #DomRoot", selB.toString())
        assertEquals(".base .test >", selC.toString())
        assertEquals("[.base .test]", selD.toString())
        assertEquals("[.extra]", selE.toString())
        assertEquals(".base .test > Room .abc .123 #DomRoot", (selA + selB + selC).toString())
        assertEquals("[.base .test] Room .abc .123 #DomRoot", (selA + selB + selD).toString())
        assertEquals("[.base .test, .extra]", (selD + selE).toString())
    }
}