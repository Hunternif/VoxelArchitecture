package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuildStats
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class DomTest {
    @Test
    fun `nested nodes`() {
        val dom = domRoot {
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
        val style = Stylesheet().add {
            style("parent") {
                height { 10.vx }
                width { 20.vx }
                depth { 30.vx }
            }
            style("child") {
                height { 75.pct }
                width { 120.pct }
                depth { 100.pct }
            }
        }
        val dom = domRoot {
            room("parent") {
                room("child")
            }
        }.buildDom(style)

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(Vec3(20, 10, 30), parent.naturalSize)
        assertEquals(Vec3(24.0, 7.5, 30.0), child.naturalSize)
    }

    @Test
    fun `nested rooms with min and max size`() {
        val style = Stylesheet().add {
            style("parent") {
                height { 10.vx }
                width { 20.vx }
                depth { 30.vx }
            }
            style("child") {
                height { min = 8.vx; 75.pct }
                width { max = 21.vx; 120.pct }
                depth { min = 1.vx; 0.pct }
            }
        }
        val dom = domRoot {
            room("parent") {
                room("child")
            }
        }.buildDom(style)

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(Vec3(21, 8, 1), child.naturalSize)
    }

    @Test
    fun `room with random size`() {
        val seed = 0L
        val style = Stylesheet().add {
            style("random") {
                height { 1.vx to 100.vx }
                width { 1.vx to 1000.vx }
                depth { 1.vx to 1000.vx }
            }
        }
        val dom = domRoot {
            room("random")
        }.buildDom(style, seed)

        val room = dom.children.first() as Room
        assertEquals(Vec3(110, 46, 144), room.naturalSize)
    }

    @Test
    fun `use parent seed`() {
        val seed = 3L
        val style = Stylesheet().add {
            style("random") {
                height { 1.vx to 1000.vx }
            }
            style("parent_seed") {
                seed { inherit() }
            }
        }
        val dom = domRoot {
            room("random")
            room("random")
            room("parent_seed", "random") {
                room("random")
            }
            room("parent_seed", "random") {
                room("random")
            }
        }.buildDom(style, seed)

        val room1 = dom.children[0] as Room
        val room2 = dom.children[1] as Room
        val room3 = dom.children[2] as Room
        val room4 = dom.children[3] as Room
        val nested1 = room3.children[0] as Room
        val nested2 = room4.children[0] as Room
        assertEquals(804.0, room1.naturalHeight, 0.0)
        assertEquals(5.0, room2.naturalHeight, 0.0)
        assertEquals(144.0, room3.naturalHeight, 0.0)
        assertEquals(144.0, room4.naturalHeight, 0.0)
        assertEquals(804.0, nested1.naturalHeight, 0.0)
        assertEquals(804.0, nested2.naturalHeight, 0.0)
    }

    @Test
    fun `dom element with multiple classes`() {
        val style = Stylesheet().add {
            style("height_100") {
                height { 100.vx }
            }
            style("width_200") {
                width { 200.vx }
            }
        }
        val dom = domRoot {
            room("height_100", "width_200")
        }.buildDom(style)

        val room = dom.children.first() as Room
        assertEquals(100.0, room.naturalHeight, 0.0)
        assertEquals(200.0, room.naturalWidth, 0.0)
    }

    @Test
    fun `multiple styles with the same name`() {
        val style = Stylesheet().add {
            styleFor<Room> {
                height { 100.vx }
            }
            styleFor<Room> {
                width { 200.vx }
            }
        }
        val dom = domRoot {
            room()
        }.buildDom(style)

        val room = dom.children.first() as Room
        assertEquals(100.0, room.naturalHeight, 0.0)
        assertEquals(200.0, room.naturalWidth, 0.0)
    }

    @Test
    fun `inherit styles from superclasses`() {
        val style = Stylesheet().add {
            styleFor<Node> {
                height { 100.vx }
            }
            styleFor<Room> {
                width { 200.vx }
            }
            styleFor<PolyRoom> {
                depth { 300.vx }
            }
        }
        val dom = domRoot {
            room()
            polyRoom()
        }.buildDom(style)

        val room = dom.children[0] as Room
        val polyRoom = dom.children[1] as PolyRoom
        assertEquals(Vec3(200, 100, 1), room.naturalSize)
        assertEquals(Vec3(200, 100, 300), polyRoom.naturalSize)
    }

    @Test
    fun `inherit style value from parent node`() {
        val style = Stylesheet().add {
            style("parent") {
                height { 100.vx }
            }
            style("child") {
                height { inherit() }
            }
        }
        val dom = domRoot {
            room("parent") {
                room("child")
            }
        }.buildDom(style)

        val parent = dom.children.first() as Room
        val child = parent.children.first() as Room
        assertEquals(100.0, parent.naturalHeight, 0.0)
        assertEquals(100.0, child.naturalHeight, 0.0)
    }

    @Test
    fun `do not inherit node tag from style class`() {
        val dom = domRoot {
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

    @Test
    fun `apply inherited style classes`() {
        val style = Stylesheet().add {
            style(selectChildOf("tall")) {
                height { 10.vx }
            }
            style("wide") {
                width { 20.vx }
            }
            style(selectDescendantOf("deep")) {
                depth { 2.vx }
            }
        }
        val dom = domRoot {
            room("parent", "tall", "deep") {
                room("child1")
                room("child2", "wide") {
                    room("child4")
                }
            }
            room("room3", "wide")
        }.buildDom(style)

        val parent = dom.query<Room>("parent").first()
        val child1 = dom.query<Room>("child1").first()
        val child2 = dom.query<Room>("child2").first()
        val child4 = dom.query<Room>("child4").first()
        val room3 = dom.query<Room>("room3").first()
        assertEquals(Vec3(1, 1, 1), parent.naturalSize)
        assertEquals(Vec3(1, 10, 2), child1.naturalSize)
        assertEquals(Vec3(20, 10, 2), child2.naturalSize)
        assertEquals(Vec3(1, 1, 2), child4.naturalSize)
        assertEquals(Vec3(20, 1, 1), room3.naturalSize)
    }

    @Test
    fun `style for instance`() {
        lateinit var instance: DomBuilder
        val domRoot = domRoot {
            room("special_room") {
                instance = this@room
            }
            room("second_room")
        }
        val style = Stylesheet().add {
            styleFor(instance) {
                height { 35.vx }
            }
        }
        val dom = domRoot.buildDom(style)

        val specialRoom = dom.query<Room>("special_room").first()
        val secondRoom = dom.query<Room>("second_room").first()
        assertEquals(35.0, specialRoom.naturalHeight, 0.0)
        assertEquals(1.0, secondRoom.naturalHeight, 0.0)
    }

    @Test
    fun `passthrough node`() {
        val dom = domRoot {
            node("parent") {
                passthrough {
                    node("child")
                }
            }
        }.buildDom()

        assertEquals(1, dom.children.size)
        val parent = dom.children.first()
        assertEquals(1, parent.children.size)
        val child = parent.children.first()
        assertEquals(0, child.children.size)
    }

    @Test
    fun `apply style on multiple-class selector`() {
        val style = Stylesheet().add {
            style("one") {
                height { 10.vx }
            }
            style("one", "two") {
                width { 20.vx }
            }
        }
        val dom = domRoot {
            room("one")
            room("one", "two")
            room("one", "two", "three")
        }.buildDom(style)

        val (room1, room2, room3) = dom.query<Room>().toList()
        assertEquals(Vec3(1, 10, 1), room1.naturalSize)
        assertEquals(Vec3(20, 10, 1), room2.naturalSize)
        assertEquals(Vec3(20, 10, 1), room3.naturalSize)
    }

    @Test
    fun `style family`() {
        val style = Stylesheet().add {
            styleFamily(select("family")) {
                style("one") {
                    height { 10.vx }
                }
                style("two") {
                    width { 20.vx }
                }
            }
        }
        val dom = domRoot {
            room("one")
            room("two")
            room("family", "one")
            room("family", "two")
        }.buildDom(style)

        val (room1, room2, room3, room4) = dom.query<Room>().toList()
        assertEquals(Vec3(1, 1, 1), room1.naturalSize)
        assertEquals(Vec3(1, 1, 1), room2.naturalSize)
        assertEquals(Vec3(1, 10, 1), room3.naturalSize)
        assertEquals(Vec3(20, 1, 1), room4.naturalSize)
    }

    @Test
    fun `deep-copy DomBuildContext`() {
        val dom1 = DomBuilder()
        val dom2 = DomBuilder()
        val ctx1 = DomBuildContext(Node(), defaultStyle, 0, DomBuildStats())
        val styled1 = dom1.prepareForLayout(ctx1)
        dom2.prepareForLayout(ctx1)
        val ctx2 = ctx1.makeChildCtx(parent = styled1)
        assertEquals(styled1, ctx2.parent)
        assertEquals(listOf(styled1), ctx2.lineage)
        assertEquals(listOf<StyledElement<*>>(), ctx1.lineage)
    }
}