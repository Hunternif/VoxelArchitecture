package hunternif.voxarch.plan

import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.assertVec3Equals
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.vector.AABB
import hunternif.voxarch.vector.IntAABB
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
    fun `local center, min & max points`() {
        val origin = Vec3(10, 20, 30)
        val node = Node(origin).apply {
            size = Vec3(2, 4, 6)
        }
        assertEquals(Vec3(1, 2, 3), node.localCenter)
        assertEquals(Vec3(10, 20, 30), node.minPoint)
        assertEquals(Vec3(12, 24, 36), node.maxPoint)

        val room = Room(origin, Vec3(2, 4, 6))
        room.setCentered(true)
        assertEquals(Vec3(0, 2, 0), room.localCenter)
        assertEquals(Vec3(9, 20, 27), room.minPoint)
        assertEquals(Vec3(11, 24, 33), room.maxPoint)

        room.start = Vec3(1, 1, 1)
        assertEquals(Vec3(2, 3, 4), room.localCenter)
        assertEquals(Vec3(11, 21, 31), room.minPoint)
        assertEquals(Vec3(13, 25, 37), room.maxPoint)

        val polyRoom = PolyRoom(origin, Vec3(2, 4, 6)).apply {
            start = Vec3(1, 1, 1)
        }
        assertEquals(Vec3(2, 3, 4), polyRoom.localCenter)
        assertEquals(Vec3(11, 21, 31), polyRoom.minPoint)
        assertEquals(Vec3(13, 25, 37), polyRoom.maxPoint)

        val roundPath = Path(origin).apply {
            ellipse(2.0, 6.0, 8)
        }
        assertVec3Equals(Vec3(0, 0, 0), roundPath.localCenter, 0.0000001)
        assertVec3Equals(Vec3(9, 20, 27), roundPath.minPoint, 0.3)
        assertVec3Equals(Vec3(11, 20, 33), roundPath.maxPoint, 0.3)

        val rectPath = Path(origin).apply {
            rectangle(2.0, 6.0)
        }
        assertEquals(Vec3(0, 0, 0), rectPath.localCenter)
        assertEquals(Vec3(9, 20, 27), rectPath.minPoint)
        assertEquals(Vec3(11, 20, 33), rectPath.maxPoint)

        // wall is oriented towards pos Z
        val wall = Wall(Vec3(10, 20, 30), Vec3(10, 24, 32))
        assertEquals(Vec3(1, 2, 0), wall.localCenter)
        assertEquals(Vec3(10, 20, 30), wall.minPoint)
        assertEquals(Vec3(10, 24, 32), wall.maxPoint)

        // wall is oriented towards neg X
        wall.rotationY = 180.0
        assertEquals(Vec3(1, 2, 0), wall.localCenter)
        assertEquals(Vec3(8, 20, 30), wall.minPoint)
        assertEquals(Vec3(10, 24, 30), wall.maxPoint)

        val floor = Floor(3.0)
        assertEquals(Vec3(0, 0, 0), floor.start)
        assertEquals(Vec3(0, 0, 0), floor.localCenter)
        assertEquals(Vec3(0, 3, 0), floor.minPoint)
        assertEquals(Vec3(0, 3, 0), floor.maxPoint)

        Room().addChild(floor)
        assertEquals(Vec3(0, 0, 0), floor.start)
        assertEquals(Vec3(0, 0, 0), floor.localCenter)
        assertEquals(Vec3(0, 3, 0), floor.minPoint)
        assertEquals(Vec3(0, 3, 0), floor.maxPoint)

        room.addChild(floor)
        assertEquals(Vec3(1, 0, 1), floor.start)
        assertEquals(Vec3(2, 0, 4), floor.localCenter)
        assertEquals(Vec3(1, 4, 1), floor.minPoint)
        assertEquals(Vec3(3, 4, 7), floor.maxPoint)

        room.start = Vec3(0, 0, 0)
        assertEquals(Vec3(0, 0, 0), floor.start)
        assertEquals(Vec3(1, 0, 3), floor.localCenter)
        assertEquals(Vec3(0, 3, 0), floor.minPoint)
        assertEquals(Vec3(2, 3, 6), floor.maxPoint)

        node.maxPoint += Vec3(1, 2, 3)
        assertEquals(Vec3(11, 22, 33), node.origin)
        assertEquals(Vec3(11, 22, 33), node.minPoint)
        assertEquals(Vec3(13, 26, 39), node.maxPoint)

        node.minPoint -= Vec3(1, 2, 3)
        assertEquals(Vec3(10, 20, 30), node.origin)
        assertEquals(Vec3(10, 20, 30), node.minPoint)
        assertEquals(Vec3(12, 24, 36), node.maxPoint)

        node.maxX += 1
        assertEquals(Vec3(11, 20, 30), node.origin)
        assertEquals(Vec3(11, 20, 30), node.minPoint)
        assertEquals(Vec3(13, 24, 36), node.maxPoint)
    }

    @Test
    fun `find AABB for straight room without walls`() {
        val room = Structure().room(
            Vec3(1, 2, 3), Vec3(10, 20, 30)
        )
        val trans = LinearTransformation()
            .translate(room.origin)
        val ref = IntAABB(1, 2, 3, 10, 20, 30)
        val aabb = room.findIntAABB(trans)
        val globalAABB = room.findGlobalAABB().toIntAABB()
        val localAABB = room.findLocalAABB().toIntAABB()
        assertEquals(ref, aabb)
        assertEquals(ref, globalAABB)
        assertEquals(ref, localAABB)
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
        val ref = IntAABB(1, 2, 3, 10, 20, 30)
        val aabb = room.findIntAABB(trans)
        val globalAABB = room.findGlobalAABB().toIntAABB()
        val localAABB = room.findLocalAABB().toIntAABB()
        assertEquals(ref, aabb)
        assertEquals(ref, globalAABB)
        assertEquals(ref, localAABB)
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

        val ref = AABB(
            -sqrt(2.0), 0.0, -sqrt(2.0),
            sqrt(2.0), 1.0, sqrt(2.0)
        )
        val aabb = room.findAABB(trans)
        val globalAABB = room.findGlobalAABB()
        val localAABB = room.findLocalAABB()
        assertEquals(ref, aabb)
        assertEquals(ref, globalAABB)
        assertEquals(ref, localAABB)

        val iaabb = room.findIntAABB(trans)
        val iref = IntAABB(-2, 0, -2, 2, 1, 2)
        assertEquals(iref, iaabb)
    }

    private fun assertEquals(ref: AABB, actual: AABB, delta: Double = 0.0001) {
        assertEquals(ref.minX, actual.minX, delta)
        assertEquals(ref.minY, actual.minY, delta)
        assertEquals(ref.minY, actual.minY, delta)
        assertEquals(ref.maxX, actual.maxX, delta)
        assertEquals(ref.maxY, actual.maxY, delta)
        assertEquals(ref.maxZ, actual.maxZ, delta)
    }

    @Test
    fun `find local AABB of rotated child`() {
        val parent = Room(Vec3(1, 20, 0), Vec3(2, 2, 4))
        val child = Room(Vec3(0, 100, 5), Vec3(10, 1, 1))
        parent.start = Vec3(0, 0, 0)
        child.start = Vec3(0, 0, 0)
        child.rotationY = 90.0

        val childLocalRef1 = IntAABB(0, 100, -5, 1, 101, 5)
        assertEquals(childLocalRef1, child.findGlobalAABB().toIntAABB())
        assertEquals(childLocalRef1, child.findLocalAABB().toIntAABB())

        val parentRef = IntAABB(1, 20, 0, 3, 22, 4)
        assertEquals(parentRef, parent.findGlobalAABB().toIntAABB())
        assertEquals(parentRef, parent.findLocalAABB().toIntAABB())

        parent.addChild(child)
        val childGlobalRef2 = IntAABB(1, 120, -5, 2, 121, 5)
        assertEquals(childGlobalRef2, child.findGlobalAABB().toIntAABB())
        assertEquals(childLocalRef1, child.findLocalAABB().toIntAABB())
    }
}