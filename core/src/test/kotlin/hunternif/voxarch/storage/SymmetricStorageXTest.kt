package hunternif.voxarch.storage

import org.junit.Assert.*
import org.junit.Test

class SymmetricStorageXTest {
    @Test
    fun `test symmetric X storage`() {
        val block = BlockData("B")
        val out = ArrayBlockStorage(5, 1, 2)
        val s = SymmetricStorageX(out, 2)

        s.setBlock(2, 0, 1, block)
        assertEquals(null, out.getBlock(2, 0, 0))
        assertEquals(block, out.getBlock(2, 0, 1))

        s.clearBlock(2, 0, 1)
        assertEquals(null, out.getBlock(2, 0, 1))

        s.setBlock(1, 0, 1, block)
        assertEquals(null, out.getBlock(1, 0, 0))
        assertEquals(block, out.getBlock(1, 0, 1))
        assertEquals(null, out.getBlock(2, 0, 1))
        assertEquals(block, out.getBlock(3, 0, 1))
        assertEquals(null, out.getBlock(3, 0, 0))
    }
}