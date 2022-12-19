package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.vector.Array3D
import org.junit.Assert.*
import org.junit.Test
import java.lang.StringBuilder

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
    fun `find patterns in 2d input`() {
        val data = Array3D(3, 1, 3) { x, y, z ->
            if (x < 2) 'a' else null
        }
        val patterns = data.findPatterns(2, 1)
        // Expected patterns:
        //  aa  a?  aa  ?a  ??
        //  aa  a?  ??  ?a  aa
        // I shouldn't really assume their returned order, but I know they will
        // maintain order thanks to LinkedHashSet
        assertEquals(5, patterns.size)
        assertPattern("aa\naa", patterns[0])
        assertPattern("a \na ", patterns[1])
        assertPattern("aa\n  ", patterns[2])
        assertPattern(" a\n a", patterns[3])
        assertPattern("  \naa", patterns[4])

        assertEquals(2.0, patterns[0].probability, 0.00001)
        for (i in 1..4)
            assertEquals(0.5, patterns[i].probability, 0.00001)
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ab
     *  | dc
     *  V
     *  Z
     * ```
     */
    @Test
    fun `use 2 reflections and 4 rotations in 2d input`() {
        val data = Array3D(2, 1, 2, ' ')
        data[0, 0, 0] = 'a'
        data[1, 0, 0] = 'b'
        data[1, 0, 1] = 'c'
        data[0, 0, 1] = 'd'
        val patterns = data.findPatterns(2, 1)
        // Expected patterns:
        //  ab  da  cd  bc  ba  cb  dc  ad
        //  dc  cb  ba  ad  cd  da  ab  bc
        assertEquals(8, patterns.size)
        assertPattern("ab\ndc", patterns[0])
        assertPattern("da\ncb", patterns[1])
        assertPattern("cd\nba", patterns[2])
        assertPattern("bc\nad", patterns[3])
        assertPattern("ba\ncd", patterns[4])
        assertPattern("cb\nda", patterns[5])
        assertPattern("dc\nab", patterns[6])
        assertPattern("ad\nbc", patterns[7])
        patterns.forEach { assertEquals(0.125, it.probability, 0.00001) }
    }
}

private fun <C : Char?> assertPattern(expected: String, pattern: WfcPattern<C>) {
    val sb = StringBuilder()
    for (z in 0 until pattern.width) {
        if (z > 0) sb.append('\n')
        for (x in 0 until pattern.length)
            sb.append(pattern[x, 0, z] ?: ' ')
    }
    assertEquals(expected, sb.toString())
}