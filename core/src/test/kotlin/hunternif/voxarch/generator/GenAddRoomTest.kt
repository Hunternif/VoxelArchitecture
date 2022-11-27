package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.plan.Room
import org.junit.Assert.assertEquals
import org.junit.Test

class GenAddRoomTest {
    @Test
    fun `add room and apply styles`() {
        val style = Stylesheet().apply {
            style2("my_room") {
                height2 { 10.vx }
            }
            style2("my_child") {
                height2 { 5.vx }
            }
        }
        val dom = DomRoot(style).apply {
            room("my_room") {
                generators.add(GenAddRoom("my_child"))
            }
        }.buildDom()

        val parent = dom.children.first() as Room
        assertEquals(1, parent.children.size)
        assertEquals(setOf("my_room"), parent.tags)
        assertEquals(10.0, parent.height, 0.0)

        val child = parent.children.first() as Room
        assertEquals(0, child.children.size)
        assertEquals(setOf("my_child"), child.tags)
        assertEquals(5.0, child.height, 0.0)
    }

    @Test
    fun `add room and call next generator`() {
        val style = Stylesheet().apply {
            style2("nested_child") {
                height2 { 2.vx }
            }
        }
        val generator = GenAddRoom()
        generator.nextGens.add(GenAddRoom("nested_child"))
        val dom = DomRoot(style).apply {
            room() {
                generators.add(generator)
            }
        }.buildDom()

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(1, child.children.size)

        val nestedChild = child.children.first() as Room
        assertEquals(0, nestedChild.children.size)
        assertEquals(setOf("nested_child"), nestedChild.tags)
        assertEquals(2.0, nestedChild.height, 0.0)
    }
}