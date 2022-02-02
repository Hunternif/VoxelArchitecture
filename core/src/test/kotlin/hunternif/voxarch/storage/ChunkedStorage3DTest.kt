package hunternif.voxarch.storage

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class ChunkedStorage3DTest {
    @Test
    fun `read and write to multiple chunks`() {
        val a = ChunkedStorage3D<Char>(2, 3, 4)
        assertEquals(null, a[0, 0, 0])
        assertEquals(null, a[123, 456, -234])
        assertEquals(0, a.chunkMap.size)

        a[0, 0, 0] = 'a'
        assertEquals(1, a.size)
        assertEquals('a', a[0, 0, 0])
        assertEquals(null, a[1, 0, 0])
        assertEquals(null, a[-1, 0, 0])
        assertEquals(1, a.chunkMap.size)

        a[-1, 0, 0] = 'b'
        assertEquals(2, a.size)
        assertEquals('a', a[0, 0, 0])
        assertEquals(null, a[1, 0, 0])
        assertEquals('b', a[-1, 0, 0])
        assertEquals(2, a.chunkMap.size)

        a[1, 2, 3] = 'c'
        assertEquals(3, a.size)
        assertEquals(2, a.chunkMap.size)
        a[-123, 456, -1] = 'd'
        assertEquals(4, a.size)
        assertEquals(3, a.chunkMap.size)
        a[-123, 456, -1] = 'e'
        assertEquals(4, a.size)
        assertEquals(3, a.chunkMap.size)

        assertEquals('a', a[0, 0, 0])
        assertEquals(null, a[1, 0, 0])
        assertEquals('b', a[-1, 0, 0])
        assertEquals(null, a[-2, 0, 0])
        assertEquals('c', a[1, 2, 3])
        assertEquals('e', a[-123, 456, -1])

        a[1, 2, 3] = null
        assertEquals(null, a[1, 2, 3])
        assertEquals(3, a.size)
        assertEquals(3, a.chunkMap.size)
    }

    @Test
    fun `reuse map key correctly`() {
        val a = ChunkedStorage3D<Char>(2, 3, 4)
        assertEquals(0, a.chunkMap.size)
        a[0, 0, 1] = 'a'
        assertEquals(1, a.chunkMap.size)
        a[0, 0, 2] = 'b'
        assertEquals(1, a.chunkMap.size)
        assertEquals('a', a[0, 0, 1])
    }

    @Test
    fun `update bounds when adding points`() {
        val a = ChunkedStorage3D<Char>(2, 3, 4)
        assertEquals(0, a.width)
        assertEquals(0, a.height)
        assertEquals(0, a.length)

        a[0, 0, 0] = 'a'
        assertEquals(1, a.width)
        assertEquals(1, a.height)
        assertEquals(1, a.length)

        a[-1, 0, 0] = 'b'
        assertEquals(2, a.width)
        assertEquals(1, a.height)
        assertEquals(1, a.length)

        a[1, 2, 3] = 'c'
        assertEquals(3, a.width)
        assertEquals(3, a.height)
        assertEquals(4, a.length)

        a[-123, 456, -1] = 'd'
        assertEquals(125, a.width)
        assertEquals(457, a.height)
        assertEquals(5, a.length)

        a[-123, 456, -1] = 'e'
        assertEquals(125, a.width)
        assertEquals(457, a.height)
        assertEquals(5, a.length)

        a[1, 2, 3] = null
        // this needs to be corrected after the shrink fix!
        assertEquals(125, a.width)
        assertEquals(457, a.height)
        assertEquals(5, a.length)
    }

    @Ignore
    @Test
    fun `update bounds when removing points`() {
        val a = ChunkedStorage3D<Char>(2, 3, 4)
        a[0, 0, 0] = 'a'
        a[-1, 0, 0] = 'b'
        a[1, 2, 3] = 'c'
        a[-123, 456, -1] = 'd'
        a[-123, 456, -1] = 'e'
        assertEquals(4, a.size)
        assertEquals(125, a.width)
        assertEquals(457, a.height)
        assertEquals(5, a.length)

        a[1, 2, 3] = null
        assertEquals(3, a.size)
        assertEquals(124, a.width)
        assertEquals(457, a.height)
        assertEquals(5, a.length)

        a[-123, 456, -1] = null
        assertEquals(2, a.size)
        assertEquals(2, a.width)
        assertEquals(1, a.height)
        assertEquals(1, a.length)

        a[-1, 0, 0] = null
        assertEquals(1, a.size)
        assertEquals(1, a.width)
        assertEquals(1, a.height)
        assertEquals(1, a.length)

        a[0, 0, 0] = null
        assertEquals(0, a.size)
        assertEquals(0, a.width)
        assertEquals(0, a.height)
        assertEquals(0, a.length)
    }
}