package hunternif.voxarch.builder

import com.nhaarman.mockitokotlin2.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RoomBuilderTest {
    lateinit var room: Room
    lateinit var floor: Floor
    lateinit var wall: Wall
    lateinit var childRoom: Room
    lateinit var prop: Prop
    lateinit var gate: Gate
    lateinit var hatch: Hatch
    lateinit var node: Node

    @Mock lateinit var world: IBlockStorage
    lateinit var buildContext: BuildContext

    @Mock lateinit var builder: Builder<Node>

    @Before
    fun setup() {
        buildContext = BuildContext(mock()).apply{
            builders.set(null to builder)
        }
        room = spy(Room(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO))
        floor = spy(Floor(Vec3.ZERO, Vec3.ZERO))
        wall = spy(Wall(Vec3.ZERO, Vec3.ZERO))
        childRoom = spy(Room(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO))
        prop = spy(Prop(Vec3.ZERO, "prop1"))
        gate = spy(Gate(Vec3.ZERO, Vec2.ZERO))
        hatch = spy(Hatch(Vec3.ZERO, Vec2.ZERO))
        node = spy(Prop(Vec3.ZERO, "prop2"))
    }

    @Test
    fun `build in correct order of classes`() {
        room.apply {
            addChild(node)
            addChild(gate)
            addChild(childRoom)
            addChild(wall)
            addChild(floor)
            addChild(hatch)
            addChild(prop)
        }
        val inOrder = inOrder(builder)

        RoomBuilder().build(room, world, buildContext)

        inOrder.verify(builder).build(eq(floor), any(), eq(buildContext))
        inOrder.verify(builder).build(eq(wall), any(), eq(buildContext))
        inOrder.verify(builder).build(eq(node), any(), eq(buildContext))
        inOrder.verify(builder).build(eq(childRoom), any(), eq(buildContext))
        inOrder.verify(builder).build(eq(prop), any(), eq(buildContext))
        inOrder.verify(builder).build(eq(gate), any(), eq(buildContext))
        inOrder.verify(builder).build(eq(hatch), any(), eq(buildContext))
    }
}