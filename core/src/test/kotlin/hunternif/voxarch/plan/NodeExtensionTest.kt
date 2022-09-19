package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class NodeExtensionTest {
    @Test
    fun finGlobalPosition() {
        val root = Node(Vec3(1, 0, 0))
        val child1 = Node(Vec3(-1, 2, 3))
        val child2 = Node(Vec3(3, 1, -6))
        root.addChild(child1)
        child1.addChild(child2)

        assertEquals(Vec3(1, 0, 0), root.findGlobalPosition())
        assertEquals(Vec3(0, 2, 3), child1.findGlobalPosition())
        assertEquals(Vec3(3, 3, -3), child2.findGlobalPosition())

        child1.origin.y += 10
        assertEquals(Vec3(0, 12, 3), child1.findGlobalPosition())
        assertEquals(Vec3(3, 13, -3), child2.findGlobalPosition())
    }

    @Test
    fun `query by class and by type`() {
        lateinit var parent: Room
        lateinit var prop1: Prop
        lateinit var prop2: Prop
        lateinit var child1: Room
        lateinit var noTags: Room
        val root = Structure().apply {
            parent = room(Vec3.ZERO, Vec3.ZERO) {
                tags += "parent_room"
                prop1 = prop(Vec3.ZERO, "prop") {
                    tags += "child"
                }
                child1 = room(Vec3.ZERO, Vec3.ZERO) {
                    tags += "child_room"
                    tags += "child"
                    prop2 = prop(Vec3.ZERO, "other_prop")
                }
                noTags = room(Vec3.ZERO, Vec3.ZERO)
            }
        }

        assertEquals(
            emptyList<Node>(),
            root.query<Node>("unknown_tag").toList()
        )
        assertEquals(
            listOf(root, parent, prop1, child1, noTags, prop2),
            root.query<Node>().toList()
        )
        assertEquals(
            listOf(parent, child1, noTags),
            root.query<Room>().toList()
        )
        assertEquals(
            listOf(prop1, prop2),
            root.query<Prop>().toList()
        )
        assertEquals(
            listOf(parent),
            root.query<Node>("parent_room").toList()
        )
        assertEquals(
            listOf(prop1, child1),
            root.query<Node>("child").toList()
        )
        assertEquals(
            listOf(child1),
            root.query<Node>("child", "child_room").toList()
        )
        assertEquals(
            listOf(child1),
            root.query<Room>("child").toList()
        )
    }
}