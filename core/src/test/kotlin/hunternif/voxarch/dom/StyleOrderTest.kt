package hunternif.voxarch.dom

import com.nhaarman.mockitokotlin2.*
import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import org.junit.Test
import org.mockito.Mockito

class StyleOrderTest {
    @Test
    fun `execute styles in order`() {
        val style = Stylesheet().add {
            style("class1") {
                width { 98.vx }
            }
            style("class2") {
                length { 50.vx }
                height { 100.vx }
            }
        }
        val node: Room = spy(Room())
        val domBuilder = DomNodeBuilder { node }
        domBuilder.addStyles("class1", "class2")
        val ctx = DomBuildContext(DomBuilder(), Structure(), defaultStyle, 0)
        val styledNode = StyledNode(node, domBuilder, ctx)

        style.applyStyle(styledNode)

        val inOrder = Mockito.inOrder(node)
        inOrder.verify(node).height = 100.0
        inOrder.verify(node).width = 98.0
        inOrder.verify(node).length = 50.0
    }
}