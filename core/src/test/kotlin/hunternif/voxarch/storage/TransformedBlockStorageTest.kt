package hunternif.voxarch.storage

import hunternif.voxarch.util.Direction
import hunternif.voxarch.vector.TransformationStack
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TransformedBlockStorageTest {
    lateinit var out: MultiDimArrayBlockStorage
    lateinit var trans: TransformationStack
    lateinit var storage: IBlockStorage

    @Before
    fun setup() {
        out = MultiDimArrayBlockStorage(1, 1, 1)
        trans = TransformationStack()
        storage = TransformedBlockStorage(out, trans)
    }

    @Test
    fun `rotate block`() {
        val block = BlockData("B")
        block.orientation = Direction.EAST

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.NORTH, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.WEST, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.SOUTH, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.EAST, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.NORTH, out.getBlock(0, 0, 0)!!.orientation)
    }

    @Test
    fun `rotate nested block`() {
        val block = BlockData("B")
        block.orientation = Direction.EAST
        trans.rotateY(90.0)
        trans.push()
        trans.rotateY(90.0)

        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.WEST, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.SOUTH, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.EAST, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.NORTH, out.getBlock(0, 0, 0)!!.orientation)

        trans.rotateY(90.0)
        storage.setBlock(0, 0, 0, block)
        assertEquals(Direction.WEST, out.getBlock(0, 0, 0)!!.orientation)
    }
}