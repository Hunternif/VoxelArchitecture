package hunternif.voxarch.dom

import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
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
        assertEquals(Vec3(21, 8, 1), child.size)
    }

    @Test
    fun `room with random size`() {
        val seed = 0L
        val style = Stylesheet().apply {
            style("random") {
                height { 1.vx to 100.vx }
                width { 1.vx to 1000.vx }
                length { 1.vx to 1000.vx }
            }
        }
        val dom = DomRoot(style, seed).apply {
            room("random")
        }.build()

        val room = dom.children.first() as Room
        assertEquals(Vec3(110, 46, 144), room.size)
    }

    @Test
    fun `use parent seed`() {
        val seed = 3L
        val style = Stylesheet().apply {
            style("random") {
                height { 1.vx to 1000.vx }
            }
            style("parent_seed") {
                useParentSeed()
            }
        }
        val dom = DomRoot(style, seed).apply {
            room("random")
            room("random")
            room("parent_seed", "random") {
                room("random")
            }
            room("parent_seed", "random") {
                room("random")
            }
        }.build()

        val room1 = dom.children[0] as Room
        val room2 = dom.children[1] as Room
        val room3 = dom.children[2] as Room
        val room4 = dom.children[3] as Room
        val nested1 = room3.children[0] as Room
        val nested2 = room4.children[0] as Room
        assertEquals(804.0, room1.height, 0.0)
        assertEquals(5.0, room2.height, 0.0)
        assertEquals(144.0, room3.height, 0.0)
        assertEquals(144.0, room4.height, 0.0)
        assertEquals(814.0, nested1.height, 0.0)
        assertEquals(163.0, nested2.height, 0.0)
    }

    @Test
    fun `multiple classes`() {
        val seed = 3L
        val style = Stylesheet().apply {
            style("height_100") {
                height { 100.vx }
            }
            style("width_200") {
                width { 200.vx }
            }
        }
        val dom = DomRoot(style, seed).apply {
            room("height_100", "width_200")
        }.build()

        val room = dom.children.first() as Room
        assertEquals(100.0, room.height, 0.0)
        assertEquals(200.0, room.width, 0.0)
    }
}