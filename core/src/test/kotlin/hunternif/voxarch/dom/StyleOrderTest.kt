package hunternif.voxarch.dom

import com.nhaarman.mockitokotlin2.*
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Room
import org.junit.Test
import org.mockito.Mockito

class StyleOrderTest {
    @Test
    fun `execute styles in order`() {
        val style = Stylesheet().apply {
            style2("class1") {
                width2 { 98.vx }
            }
            style2("class2") {
                length2 { 50.vx }
                height2 { 100.vx }
            }
        }
        val node: Room = spy(Room())
        val domBuilder = DomNodeBuilder { node }
        DomRoot().addChild(domBuilder)

        style.apply(domBuilder, listOf("class1", "class2"))

        val inOrder = Mockito.inOrder(node)
        inOrder.verify(node).height = 100.0
        inOrder.verify(node).width = 98.0
        inOrder.verify(node).length = 50.0
    }
}