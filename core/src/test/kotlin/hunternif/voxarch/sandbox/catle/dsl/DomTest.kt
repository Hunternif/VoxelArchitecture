package hunternif.voxarch.sandbox.catle.dsl

import hunternif.voxarch.plan.Room
import hunternif.voxarch.sandbox.castle.dsl.*
import org.junit.Assert.*
import org.junit.Test

class DomTest {
    @Test
    fun `nested nodes`() {
        val dom = DomRoot().apply {
            node("parent") {
                node("child")
            }
        }.build()

        assertEquals(1, dom.children.size)
        val parent = dom.children.first()
        assertEquals(1, parent.children.size)
        val child = parent.children.first()
        assertEquals(0, child.children.size)
    }

    @Test
    fun `nested rooms with styled size`() {
        val style = Stylesheet().apply {
            style("parent") {
                height { 10.vx }
                width { 20.vx }
                length { 30.vx }
            }
            style("child") {
                height { 75.pct }
                width { 120.pct }
                length { 100.pct }
            }
        }
        val dom = DomRoot(style).apply {
            room("parent") {
                room("child")
            }
        }.build()

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(10.0, parent.height, 0.0)
        assertEquals(20.0, parent.width, 0.0)
        assertEquals(30.0, parent.length, 0.0)
        assertEquals(7.5, child.height, 0.0)
        assertEquals(24.0, child.width, 0.0)
        assertEquals(30.0, child.length, 0.0)
    }

    @Test
    fun `nested rooms with min and max size`() {
        val style = Stylesheet().apply {
            style("parent") {
                height { 10.vx }
                width { 20.vx }
                length { 30.vx }
            }
            style("child") {
                height {  min = 8; 75.pct }
                width { max = 21; 120.pct }
                length { min = 1; 0.pct }
            }
        }
        val dom = DomRoot(style).apply {
            room("parent") {
                room("child")
            }
        }.build()

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(8.0, child.height, 0.0)
        assertEquals(21.0, child.width, 0.0)
        assertEquals(1.0, child.length, 0.0)
    }
}