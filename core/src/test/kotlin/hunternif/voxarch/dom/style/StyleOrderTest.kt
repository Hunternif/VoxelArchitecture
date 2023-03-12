package hunternif.voxarch.dom.style

import com.nhaarman.mockitokotlin2.spy
import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StyleOrderTest {
    @Test
    fun `execute styles in order`() {
        val calls = mutableListOf<String>()
        val style = Stylesheet().add {
            style("class1") {
                width { number { _, _ -> calls.add("width"); 1.0 } }
            }
            style("class2") {
                depth { number { _, _ -> calls.add("length"); 2.0 } }
                height { number { _, _ -> calls.add("height"); 3.0 } }
            }
        }
        val node: Room = spy(Room())
        val domBuilder = DomNodeBuilder { node }
        domBuilder.addStyles("class1", "class2")
        val ctx = DomBuildContext(Structure(), defaultStyle, 0)
        val styledNode = StyledNode(node, domBuilder, ctx)

        style.applyStyle(styledNode)

        assertEquals(listOf("height", "width", "length"), calls)
    }

    @Test
    fun `ensure valid property names`() {
        // This matches ID literal in StyleGrammar
        val regex = Regex("[A-Za-z_][A-Za-z0-9_-]*")
        for (prop in GlobalStyleOrder) {
            assertTrue(
                "invalid property name ${prop.name}",
                regex.matches(prop.name),
            )
        }
    }
}