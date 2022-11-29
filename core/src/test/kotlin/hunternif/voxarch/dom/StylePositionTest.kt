package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Room
import org.junit.Assert.*
import org.junit.Test

class StylePositionTest {
    @Test
    fun `vertical offset`() {
        val style = Stylesheet().apply {
            style2("parent") { height2 { 100.vx } }
            style2("child") {
                height2 { 50.vx }
                y2 { 100.pct - 3.vx }
            }
        }
        val dom = DomRoot(style).apply {
            room("parent") {
                room("child")
            }
        }.buildDom()

        val parent = dom.children[0] as Room
        val child = parent.children[0] as Room

        assertEquals(100.0, parent.height, 0.0)
        assertEquals(47.0, child.origin.y, 0.0)
    }
}