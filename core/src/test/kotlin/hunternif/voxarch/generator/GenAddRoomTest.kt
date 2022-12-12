package hunternif.voxarch.generator

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.gen
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
import org.junit.Assert.assertEquals
import org.junit.Test

class GenAddRoomTest {
    @Test
    fun `add room and apply styles`() {
        val style = Stylesheet().add {
            style("my_room") {
                height { 10.vx }
            }
            style("my_child") {
                height { 5.vx }
            }
        }
        val dom = domRoot(style) {
            room("my_room") {
                gen(GenAddRoom("my_child"))
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
        val style = Stylesheet().add {
            style("nested_child") {
                height { 2.vx }
            }
        }
        val generator = GenAddRoom()
        generator.nextGens.add(GenAddRoom("nested_child"))
        val dom = domRoot(style) {
            room {
                gen(generator)
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