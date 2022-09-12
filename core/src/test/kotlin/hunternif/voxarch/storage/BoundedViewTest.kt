package hunternif.voxarch.storage

import hunternif.voxarch.util.assertStorageEquals
import hunternif.voxarch.vector.Array3D
import org.junit.Assert.*
import org.junit.Test

class BoundedViewTest {
    @Test
    fun `bounded view of Chunked storage`() {
        val storage = ChunkedStorage3D<Char>()
        storage[-1, -1, -1] = 'a'
        storage[2, 3, 4] = 'b'

        val bounded = storage.boundedView()
        assertEquals(4, bounded.width)
        assertEquals(5, bounded.height)
        assertEquals(6, bounded.length)
        assertEquals(0, bounded.minX)
        assertEquals(0, bounded.minY)
        assertEquals(0, bounded.minZ)
        assertEquals(3, bounded.maxX)
        assertEquals(4, bounded.maxY)
        assertEquals(5, bounded.maxZ)
        assertEquals('a', bounded[0, 0, 0])
        assertEquals(null, bounded[1, 1, 1])
        assertEquals('b', bounded[3, 4, 5])

        bounded[1, 1, 2] = 'c'
        assertEquals('c', bounded[1, 1, 2])
        assertEquals('c', storage[0, 0, 1])
    }

    @Test
    fun `bounded view of Array3D`() {
        val storage = Array3D<Char?>(10, 10, 10, null)
        storage[2, 3, 4] = 'a'
        storage[3, 4, 5] = 'b'

        val bounded = storage.boundedView()
        assertStorageEquals(storage, bounded)
    }
}