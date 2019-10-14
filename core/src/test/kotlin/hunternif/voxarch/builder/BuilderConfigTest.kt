package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Prop
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BuilderConfigTest {
    lateinit var config: BuilderConfig

    private val nodeBuilder = Builder<Node>()
    private val roomBuilder = RoomBuilder()
    private val specialRoomBuilder = RoomBuilder()
    private val floorBuilder = Builder<Floor>()

    @Before
    fun setup() {
        config = BuilderConfig()
        config.set(
            "room type" to specialRoomBuilder,
            null to roomBuilder
        )
        config.set(
            "node type" to nodeBuilder
        )
        config.set(
            null to floorBuilder
        )
    }

    @Test
    fun `get exact class and type`() {
        val node = Room(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO).apply {
            type = "room type"
        }
        assertEquals(specialRoomBuilder, config.get(node))
    }

    @Test
    fun `get exact class and default type`() {
        val node = Room(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO)
        assertEquals(roomBuilder, config.get(node))
    }

    @Test
    fun `get superclass`() {
        val room = Room(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO)
        val node = Floor.RoomBound(room, 0.0)
        assertEquals(floorBuilder, config.get(node))
    }

    @Test
    fun `get null because no builder set`() {
        val node = Prop(Vec3.ZERO)
        assertNull(config.get(node))
    }
}