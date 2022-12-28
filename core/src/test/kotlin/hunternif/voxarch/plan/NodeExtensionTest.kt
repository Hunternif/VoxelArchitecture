package hunternif.voxarch.plan

import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.assertVec3Equals
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.vector.LinearTransformation
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.sqrt

class NodeExtensionTest {
    @Test
    fun `find global position forward`() {
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
    fun `find global position and rotation in rotated nodes`() {
        val root1 = Node(Vec3(1, 0, 0)).apply { rotationY = 45.0 }
        val child1 = Node(Vec3(1, 0, 0)).apply { rotationY = 180.0 }
        val child2 = Node(Vec3(1, 0, 0))
        root1.addChild(child1)
        child1.addChild(child2)

        assertEquals(Vec3(1, 0, 0), root1.findGlobalPosition())
        assertEquals(Vec3(1 + 1.0 / sqrt(2.0), 0.0, -1.0 / sqrt(2.0)), child1.findGlobalPosition())
        assertEquals(Vec3(1, 0, 0), child2.findGlobalPosition())
        assertEquals(45.0, root1.findGlobalRotation(), 0.000000001)
        assertEquals(225.0, child1.findGlobalRotation(), 0.000000001)
        assertEquals(225.0, child2.findGlobalRotation(), 0.000000001)

        val root90 = Node(Vec3(1, 0, 0)).apply { rotationY = 90.0 }
        val child90 = Node(Vec3(1, 0, 0)).apply { rotationY = -90.0 }
        val child3 = Node(Vec3(1, 0, 0))
        root90.addChild(child90)
        child90.addChild(child3)

        assertEquals(Vec3(1, 0, 0), root90.findGlobalPosition())
        assertEquals(Vec3(1, 0, -1), child90.findGlobalPosition())
        assertEquals(Vec3(2, 0, -1), child3.findGlobalPosition())
        assertEquals(90.0, root90.findGlobalRotation(), 0.000000001)
        assertEquals(0.0, child90.findGlobalRotation(), 0.000000001)
        assertEquals(0.0, child3.findGlobalRotation(), 0.000000001)
    }

    @Test
    fun `find global position and rotation in a loop`() {
        val wall1 = Node(Vec3(1, 0, 0)).apply { rotationY = 90.0 }
        val wall2 = Node(Vec3(1, 0, 0)).apply { rotationY = 90.0 }
        val wall3 = Node(Vec3(1, 0, 0)).apply { rotationY = 90.0 }
        val wall4 = Node(Vec3(1, 0, 0)).apply { rotationY = 90.0 }
        wall1.addChild(wall2)
        wall2.addChild(wall3)
        wall3.addChild(wall4)

        assertEquals(Vec3(1, 0, 0), wall1.findGlobalPosition())
        assertEquals(Vec3(1, 0, -1), wall2.findGlobalPosition())
        assertEquals(Vec3(0, 0, -1), wall3.findGlobalPosition())
        assertEquals(Vec3(0, 0, 0), wall4.findGlobalPosition())
        assertEquals(90.0, wall1.findGlobalRotation(), 0.000000001)
        assertEquals(180.0, wall2.findGlobalRotation(), 0.000000001)
        assertEquals(270.0, wall3.findGlobalRotation(), 0.000000001)
        assertEquals(0.0, wall4.findGlobalRotation(), 0.000000001)
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

    @Test
    fun `local center`() {
        val origin = Vec3(11, 34, 76)
        val node = Node(origin).apply {
            size = Vec3(2, 4, 6)
        }
        assertEquals(Vec3(1, 2, 3), node.localCenter)

        val room = Room(origin, Vec3(2, 4, 6)).apply {
            start = Vec3(1, 1, 1)
        }
        assertEquals(Vec3(2, 3, 4), room.localCenter)

        val polyRoom = PolyRoom(origin, Vec3(2, 4, 6)).apply {
            start = Vec3(1, 1, 1)
        }
        assertEquals(Vec3(2, 3, 4), polyRoom.localCenter)

        val roundPath = Path(origin).apply {
            ellipse(2.0, 6.0, 8)
        }
        assertVec3Equals(Vec3(0, 0, 0), roundPath.localCenter, 0.0000001)

        val rectPath = Path(origin).apply {
            rectangle(2.0, 6.0)
        }
        assertEquals(Vec3(0, 0, 0), rectPath.localCenter)

        val wall = Wall(Vec3(10, 20, 30), Vec3(10, 24, 32))
        assertEquals(Vec3(1, 2, 0), wall.localCenter)

        val floor = Floor(3.0)
        assertEquals(Vec3(0, 0, 0), floor.localCenter)

        Room().addChild(floor)
        assertEquals(Vec3(0, 0, 0), floor.localCenter)

        room.addChild(floor)
        assertEquals(Vec3(2, 0, 4), floor.localCenter)
    }

    @Test
    fun `find AABB for straight room without walls`() {
        val room = Structure().room(
            Vec3(1, 2, 3), Vec3(10, 20, 30)
        )
        val trans = LinearTransformation()
            .translate(room.origin)
        val aabb = room.findIntAABB(trans)
        assertEquals(1, aabb.minX)
        assertEquals(2, aabb.minY)
        assertEquals(3, aabb.minZ)
        assertEquals(10, aabb.maxX)
        assertEquals(20, aabb.maxY)
        assertEquals(30, aabb.maxZ)
    }

    @Test
    fun `find AABB for straight room with walls`() {
        val room = Structure().room(
            Vec3(1, 2, 3), Vec3(10, 20, 30)
        ) {
            createFourWalls()
        }
        val trans = LinearTransformation()
            .translate(room.origin)
        val aabb = room.findIntAABB(trans)
        assertEquals(1, aabb.minX)
        assertEquals(2, aabb.minY)
        assertEquals(3, aabb.minZ)
        assertEquals(10, aabb.maxX)
        assertEquals(20, aabb.maxY)
        assertEquals(30, aabb.maxZ)
    }

    @Test
    fun `find AABB for room rotated 45 deg`() {
        val room = Structure().centeredRoom(
            Vec3(0, 0, 0), Vec3(2, 1, 2)
        ) {
            createFourWalls()
            rotationY = 45.0
        }
        val trans = LinearTransformation()
            .translate(room.origin)
            .rotateY(room.rotationY)

        val aabb = room.findAABB(trans)
        assertEquals(-sqrt(2.0), aabb.minX, 0.0001)
        assertEquals(0.0, aabb.minY, 0.0001)
        assertEquals(-sqrt(2.0), aabb.minZ, 0.0001)
        assertEquals(sqrt(2.0), aabb.maxX, 0.0001)
        assertEquals(1.0, aabb.maxY, 0.0001)
        assertEquals(sqrt(2.0), aabb.maxZ, 0.0001)

        val iaabb = room.findIntAABB(trans)
        assertEquals(-2, iaabb.minX)
        assertEquals(0, iaabb.minY)
        assertEquals(-2, iaabb.minZ)
        assertEquals(2, iaabb.maxX)
        assertEquals(1, iaabb.maxY)
        assertEquals(2, iaabb.maxZ)
    }
}