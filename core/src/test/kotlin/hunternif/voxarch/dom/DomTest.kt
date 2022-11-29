package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
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
        }.buildDom()

        assertEquals(1, dom.children.size)
        val parent = dom.children.first()
        assertEquals(1, parent.children.size)
        val child = parent.children.first()
        assertEquals(0, child.children.size)
    }

    @Test
    fun `nested rooms with styled size`() {
        val style = Stylesheet().apply {
            style2("parent") {
                height2 { 10.vx }
                width2 { 20.vx }
                length2 { 30.vx }
            }
            style2("child") {
                height2 { 75.pct }
                width2 { 120.pct }
                length2 { 100.pct }
            }
        }
        val dom = DomRoot(style).apply {
            room("parent") {
                room("child")
            }
        }.buildDom()

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(10.0, parent.height, 0.0)
        assertEquals(20.0, parent.width, 0.0)
        assertEquals(30.0, parent.length, 0.0)
        assertEquals(8.0, child.height, 0.0) // rounded
        assertEquals(24.0, child.width, 0.0)
        assertEquals(30.0, child.length, 0.0)
    }

    @Test
    fun `nested rooms with min and max size`() {
        val style = Stylesheet().apply {
            style2("parent") {
                height2 { 10.vx }
                width2 { 20.vx }
                length2 { 30.vx }
            }
            style2("child") {
                height2 { min = 8.vx; 75.pct }
                width2 { max = 21.vx; 120.pct }
                length2 { min = 1.vx; 0.pct }
            }
        }
        val dom = DomRoot(style).apply {
            room("parent") {
                room("child")
            }
        }.buildDom()

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(Vec3(21, 8, 1), child.size)
    }

    @Test
    fun `room with random size`() {
        val seed = 0L
        val style = Stylesheet().apply {
            style2("random") {
                height2 { 1.vx to 100.vx }
                width2 { 1.vx to 1000.vx }
                length2 { 1.vx to 1000.vx }
            }
        }
        val dom = DomRoot(style, seed).apply {
            room("random")
        }.buildDom()

        val room = dom.children.first() as Room
        assertEquals(Vec3(110, 46, 144), room.size)
    }

    @Test
    fun `use parent seed`() {
        val seed = 3L
        val style = Stylesheet().apply {
            style2("random") {
                height2 { 1.vx to 1000.vx }
            }
            style2("parent_seed") {
                seed2 { inherit() }
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
        }.buildDom()

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
    fun `dom element with multiple classes`() {
        val style = Stylesheet().apply {
            style2("height_100") {
                height2 { 100.vx }
            }
            style2("width_200") {
                width2 { 200.vx }
            }
        }
        val dom = DomRoot(style).apply {
            room("height_100", "width_200")
        }.buildDom()

        val room = dom.children.first() as Room
        assertEquals(100.0, room.height, 0.0)
        assertEquals(200.0, room.width, 0.0)
    }

    @Test
    fun `multiple styles with the same name`() {
        val style = Stylesheet().apply {
            style2For<Room> {
                height2 { 100.vx }
            }
            style2For<Room> {
                width2 { 200.vx }
            }
        }
        val dom = DomRoot(style).apply {
            room()
        }.buildDom()

        val room = dom.children.first() as Room
        assertEquals(100.0, room.height, 0.0)
        assertEquals(200.0, room.width, 0.0)
    }

    @Test
    fun `inherit styles from superclasses`() {
        val style = Stylesheet().apply {
            style2For<Node> {
                height2 { 100.vx }
            }
            style2For<Room> {
                width2 { 200.vx }
            }
            style2For<PolygonRoom> {
                length2 { 300.vx }
            }
        }
        val dom = DomRoot(style).apply {
            room()
            polygonRoom()
        }.buildDom()

        val room = dom.children[0] as Room
        val polyRoom = dom.children[1] as PolygonRoom
        assertEquals(Vec3(200, 100, 0), room.size)
        assertEquals(Vec3(200, 100, 300), polyRoom.size)
    }

    @Test
    fun `find parent node`() {
        var root: Structure
        var mid: Node
        DomRoot().apply {
            root = node
            empty {
                assertEquals(root, findParentNode())
                node {
                    assertEquals(root, findParentNode())
                    mid = node
                    room {
                        assertEquals(mid, findParentNode())
                    }
                }
            }
        }
    }

    @Test
    fun `inherit style value from parent node`() {
        val style = Stylesheet().apply {
            style2("parent") {
                height2 { 100.vx }
            }
            style2("child") {
                height2 { inherit() }
            }
        }
        val dom = DomRoot(style).apply {
            room("parent") {
                room("child")
            }
        }.buildDom()

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(100.0, parent.height, 0.0)
        assertEquals(100.0, child.height, 0.0)
    }

    @Test
    fun `do not inherit node tag from style class`() {
        val dom = DomRoot().apply {
            node("parent") {
                node("child", "extra class") {
                    node("inner")
                    node {
                        node()
                        node("innermost")
                    }
                }
            }
        }.buildDom()

        val parent = dom.children.first()
        val child = parent.children.first()
        val inner = child.children[0]
        val noclass = child.children[1]
        val noclass2 = noclass.children[0]
        val innermost = noclass.children[1]
        assertEquals(setOf("parent"), parent.tags)
        assertEquals(setOf("child", "extra class"), child.tags)
        assertEquals(setOf("inner"), inner.tags)
        assertEquals(emptySet<String>(), noclass.tags)
        assertEquals(emptySet<String>(), noclass2.tags)
        assertEquals(setOf("innermost"), innermost.tags)
    }

//    @Test
//    fun `inherit style class`() {
//        val style = Stylesheet().apply {
//            style("parent") {
//                height { 100.vx }
//            }
//            style("child") {
//                height { 50.pct }
//            }
//        }
//        val dom = DomRoot(style).apply {
//            room("parent") {
//                room()
//                room("child", "extra class") {
//                    room()
//                }
//            }
//        }.buildDom()
//
//        val parent = dom.children.first() as Room
//        val child1 = parent.children[0] as Room
//        val child2 = parent.children[1] as Room
//        val child3 = child2.children[0] as Room
//        assertEquals("parent", parent.type)
//        assertEquals(100.0, parent.height, 0.0)
//        assertEquals("parent", child1.type)
//        assertEquals(100.0, child1.height, 0.0)
//        assertEquals("child", child2.type)
//        assertEquals(50.0, child2.height, 0.0)
//        assertEquals("child", child3.type)
//        assertEquals(25.0, child3.height, 0.0)
//    }

    companion object {
        /** Creates empty logic element for testing. */
        private fun DomBuilder.empty(
            block: DomBuilder.() -> Unit = {}
        ) {
            val bld = EmptyLogicBuilder()
            addChild(bld)
            bld.block()
        }

        class EmptyLogicBuilder: DomBuilder()
    }
}