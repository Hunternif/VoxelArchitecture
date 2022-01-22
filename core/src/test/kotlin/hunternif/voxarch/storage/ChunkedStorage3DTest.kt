package hunternif.voxarch.storage

import org.junit.Assert.*
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
}