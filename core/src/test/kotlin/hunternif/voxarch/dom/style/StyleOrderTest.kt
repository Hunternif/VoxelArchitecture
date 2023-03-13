package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.property.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StyleOrderTest {
    @Test
    fun `properties from different groups use global order`() {
        val style = LoggingStylesheet()
        style.add {
            style("class1") {
                rotation { set(1.0) }
            }
            style("class2") {
                seed { set(2) }
                height { 3.vx }
            }
        }
        domRoot {
            node("class1", "class2")
        }.buildDom(style)

        assertEquals(listOf(PropSeed, PropRotation, PropHeight), style.propertyOrder)
    }

    @Test
    fun `properties within the same group are not reordered`() {
        val style = LoggingStylesheet()
        style.add {
            style("class1") {
                paddingRightX { 1.vx }
                width { 2.vx }
            }
            style("class2") {
                depth { 3.vx }
                height { 4.vx }
                paddingY { 5.vx }
            }
        }
        domRoot {
            node("class1", "class2")
        }.buildDom(style)

        assertEquals(
            listOf(PropWidth, PropDepth, PropHeight, PropPaddingRightX, PropPaddingY),
            style.propertyOrder,
        )
    }

    @Test
    fun `ensure valid property names`() {
        // This matches ID literal in StyleGrammar
        val regex = Regex("[A-Za-z_][A-Za-z0-9_-]*")
        for (prop in AllStyleProperties) {
            assertTrue(
                "invalid property name ${prop.name}",
                regex.matches(prop.name),
            )
        }
    }

    private class LoggingStylesheet : Stylesheet() {
        val propertyOrder = mutableListOf<Property<*>>()

        override fun applyStyle(element: StyledElement<*>) {
            getProperties(element).forEach {
                propertyOrder.add(it.property)
                it.applyTo(element)
            }
        }
    }
}