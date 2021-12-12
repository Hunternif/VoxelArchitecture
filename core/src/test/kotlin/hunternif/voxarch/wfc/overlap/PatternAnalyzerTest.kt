package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.vector.Array3D
import org.junit.Assert.*
import org.junit.Test

class PatternAnalyzerTest {
    /**
     * ```
     * Y
     *  +------> X
     *  | aa?
     *  | aa?
     *  | aa?
     *  V
     *  Z
     * ```
     */
    @Test
    fun `remove duplicates in 2d input`() {
        val data = Array3D(3, 1, 3) { x, y, z ->
            if (x < 2) 'a' else null
        }
        val patterns = data.findPatterns(2, 1)
        // Expected patterns:
        //  aa   a?
        //  aa   a?
        // I shouldn't really assume their returned order, but I know they will
        // maintain order thanks to LinkedHashSet
        assertEquals(2, patterns.size)
        val p1 = patterns[0]
        val p2 = patterns[1]
        assertEquals('a', p1[0, 0, 0])
        assertEquals('a', p1[0, 0, 1])
        assertEquals('a', p1[1, 0, 0])
        assertEquals('a', p1[1, 0, 1])

        assertEquals('a', p2[0, 0, 0])
        assertEquals('a', p2[0, 0, 1])
        assertEquals(null, p2[1, 0, 0])
        assertEquals(null, p2[1, 0, 1])
    }
}