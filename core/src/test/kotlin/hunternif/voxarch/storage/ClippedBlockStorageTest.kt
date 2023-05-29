package hunternif.voxarch.storage

import hunternif.voxarch.plan.ClipMask
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ClippedBlockStorageTest {
    lateinit var out: ArrayBlockStorage
    lateinit var room: Room
    lateinit var storage: IBlockStorage
    val block = BlockData("B")

    @Before
    fun setup() {
        out = ArrayBlockStorage(2, 2, 2)
        room = Room(Vec3(0, 0, 0), Vec3(1, 1, 0))
        storage = ClippedBlockStorage(out, room)
    }

    @Test
    fun `clip mask off`() {
        room.clipMask = ClipMask.OFF
        storage.setBlock(0, 0, 0, block)
        storage.setBlock(0, 0, 1, block)
        assertEquals(block, storage.getBlock(0, 0, 0))
        assertEquals(block, storage.getBlock(0, 0, 1))

        storage.clearBlock(0, 0, 0)
        storage.clearBlock(0, 0, 1)
        assertEquals(null, storage.getBlock(0, 0, 0))
        assertEquals(null, storage.getBlock(0, 0, 1))
    }

    @Test
    fun `clip mask box`() {
        room.clipMask = ClipMask.BOX
        storage.setBlock(0, 0, 0, block)
        storage.setBlock(0, 0, 1, block)
        assertEquals(block, storage.getBlock(0, 0, 0))
        assertEquals(null, storage.getBlock(0, 0, 1))

        // Fill all blocks and try to clear
        out.forEach { out.setBlock(it, block) }
        storage.clearBlock(0, 0, 0)
        storage.clearBlock(0, 0, 1)
        assertEquals(null, storage.getBlock(0, 0, 0))
        assertEquals(block, storage.getBlock(0, 0, 1))
    }

    @Test
    fun `clip mask boundary`() {
        //TODO: make a more complicated test for boundary
        room.clipMask = ClipMask.BOUNDARY
        storage.setBlock(0, 0, 0, block)
        storage.setBlock(0, 0, 1, block)
        assertEquals(block, storage.getBlock(0, 0, 0))
        assertEquals(null, storage.getBlock(0, 0, 1))

        // Fill all blocks and try to clear
        out.forEach { out.setBlock(it, block) }
        storage.clearBlock(0, 0, 0)
        storage.clearBlock(0, 0, 1)
        assertEquals(null, storage.getBlock(0, 0, 0))
        assertEquals(block, storage.getBlock(0, 0, 1))
    }
}