package hunternif.voxarch.builder

import hunternif.voxarch.plan.*
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
        config.set<Room>(
            "room tag" to specialRoomBuilder,
            null to roomBuilder
        )
        config.set<Node>(
            "node tag" to nodeBuilder
        )
        config.set<Floor>(
            null to floorBuilder
        )
    }

    @Test
    fun `get exact class and tag`() {
        val node = Room(Vec3.ZERO, Vec3.ZERO).apply {
            tags += "room tag"
        }
        assertEquals(specialRoomBuilder, config.get(node))
    }

    @Test
    fun `get exact class and default tag`() {
        val node = Room(Vec3.ZERO, Vec3.ZERO)
        assertEquals(roomBuilder, config.get(node))
    }

    @Test
    fun `get superclass`() {
        val room = Room(Vec3.ZERO, Vec3.ZERO)
        val node = room.floor()
        assertEquals(floorBuilder, config.get(node))
    }

    @Test
    fun `get null because no builder set`() {
        val node = Prop(Vec3.ZERO, "type")
        assertNull(config.get(node))
    }

    @Test
    fun `local override builder takes priority`() {
        val node = Room()
        assertEquals(roomBuilder, config.get(node))

        node.builder = specialRoomBuilder
        assertEquals(specialRoomBuilder, config.get(node))
    }
}