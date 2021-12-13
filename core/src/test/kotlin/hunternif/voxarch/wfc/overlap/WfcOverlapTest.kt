package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.util.IRandomOption
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.wfc.WfcModel
import org.junit.Assert.*
import org.junit.Test
import java.lang.StringBuilder

class WfcOverlapTest {
    /**
     * ```
     * Y
     *  +------> X
     *  | ?a??
     *  V
     *  Z
     *
     *  tiles: aa, bb
     * ```
     */
    @Test
    fun `set color on 1d wave and apply single matching pattern`() {
        val aa = Pattern1D("aa")
        val bb = Pattern1D("bb")
        val wave = WfcOverlapModel(4, 1, 1, listOf(aa, bb))

        wave[1, 0, 0] = 'a'
        // ?a??
        assertEquals(null, wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals(null, wave[2, 0, 0])
        assertEquals(null, wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa)
        wave.assertPatterns(1, 0, 0, aa)
        wave.assertPatterns(2, 0, 0, aa, bb)
        wave.assertPatterns(3, 0, 0, aa, bb)

        wave.propagate()
        // aaaa
        assertEquals('a', wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('a', wave[2, 0, 0])
        assertEquals('a', wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa)
        wave.assertPatterns(1, 0, 0, aa)
        wave.assertPatterns(2, 0, 0, aa)
        wave.assertPatterns(3, 0, 0, aa)
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ?ab?
     *  V
     *  Z
     *
     *  tiles: aa, ab, bb
     * ```
     */
    @Test
    fun `set color on 1d wave and propagate 2 patterns`() {
        val aa = Pattern1D("aa")
        val ab = Pattern1D("ab")
        val bb = Pattern1D("bb")
        val wave = WfcOverlapModel(4, 1, 1, listOf(aa, ab, bb))

        wave[1, 0, 0] = 'a'
        // ?a??
        assertEquals(null, wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals(null, wave[2, 0, 0])
        assertEquals(null, wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa)
        wave.assertPatterns(1, 0, 0, aa, ab)
        wave.assertPatterns(2, 0, 0, aa, ab, bb)
        wave.assertPatterns(3, 0, 0, aa, ab, bb)

        wave[2, 0, 0] = 'b'
        // ?ab?
        assertEquals(null, wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('b', wave[2, 0, 0])
        assertEquals(null, wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa)
        wave.assertPatterns(1, 0, 0, ab)
        wave.assertPatterns(2, 0, 0, bb)
        wave.assertPatterns(3, 0, 0, aa, ab, bb)

        wave.propagate()
        // aabb
        assertEquals('a', wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('b', wave[2, 0, 0])
        assertEquals('b', wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa)
        wave.assertPatterns(1, 0, 0, ab)
        wave.assertPatterns(2, 0, 0, bb)
        wave.assertPatterns(3, 0, 0, bb)
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ???
     *  V
     *  Z
     *
     *  tiles: aa, bb
     * ```
     */
    @Test
    fun `observe 1d wave from empty`() {
        val aa = Pattern1D("aa")
        val bb = Pattern1D("bb")
        val wave = WfcOverlapModel(3, 1, 1, listOf(aa, bb), 1)

        wave.observeStep()
        // aaa <- seed=1
        assertEquals('a', wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('a', wave[2, 0, 0])
        wave.assertPatterns(0, 0, 0, aa)
        wave.assertPatterns(1, 0, 0, aa)
        wave.assertPatterns(2, 0, 0, aa)
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ?????
     *  V
     *  Z
     *
     *  tiles: bab
     * ```
     */
    @Test
    fun `contradiction because no patterns in the middle of long pattern`() {
        val bab = Pattern1D("bab")
        val wave = WfcOverlapModel(5, 1, 1, listOf(bab))

        wave.observeStep()
        // bab??
        assertEquals('b', wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('b', wave[2, 0, 0])
        assertEquals(null, wave[3, 0, 0])
        assertEquals(null, wave[4, 0, 0])
        wave.assertPatterns(0, 0, 0, bab)
        wave.assertPatterns(1, 0, 0)
        wave.assertPatterns(2, 0, 0, bab)
        wave.assertPatterns(3, 0, 0, bab)
        wave.assertPatterns(4, 0, 0, bab)
        assertEquals(true, wave.isContradicted)
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ?????
     *  V
     *  Z
     *
     *  tiles: aba, bab
     * ```
     */
    @Test
    fun `observe 1d wave with long patterns`() {
        val aba = Pattern1D("aba")
        val bab = Pattern1D("bab")
        val wave = WfcOverlapModel(5, 1, 1, listOf(aba, bab), 1)

        wave.observeStep()
        // ababa
        assertEquals('a', wave[0, 0, 0])
        assertEquals('b', wave[1, 0, 0])
        assertEquals('a', wave[2, 0, 0])
        assertEquals('b', wave[3, 0, 0])
        assertEquals('a', wave[4, 0, 0])
        wave.assertPatterns(0, 0, 0, aba)
        wave.assertPatterns(1, 0, 0, bab)
        wave.assertPatterns(2, 0, 0, aba)
        wave.assertPatterns(3, 0, 0, bab)
        wave.assertPatterns(4, 0, 0, aba)
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ?????
     *  | ?????
     *  | ??b??
     *  | ?????
     *  | ?????
     *  V
     *  Z
     *
     *  tiles: aba, bab
     * ```
     */
    @Test
    fun `observe 2d wave`() {
        //  aa  ab  ba
        //  aa  ab  ba
        val aaaa = Pattern2D("aa"+"aa")
        val abab = Pattern2D("ab"+"ab")
        val baba = Pattern2D("ba"+"ba")
        val wave = WfcOverlapModel(5, 1, 5, listOf(aaaa, abab, baba), 8)

        wave[2, 0, 2] = 'b'
        // ?????
        // ?????
        // ??b??
        // ?????
        // ?????
        assertEquals('b', wave[2, 0, 2])
        assertEquals(null, wave[3, 0, 2])

        wave.observeStep()
        // ?????    ?aba?
        // ?????    ?aba?
        // ??ba? -> ?aba?
        // ??ba?    ?aba?
        // ?????    ?aba?
        for (z in 0..4) {
            assertEquals(null, wave[0, 0, z])
            assertEquals('a', wave[1, 0, z])
            assertEquals('b', wave[2, 0, z])
            assertEquals('a', wave[3, 0, z])
            assertEquals(null, wave[4, 0, z])
            wave.assertPatterns(0, 0, z, aaaa, baba)
            wave.assertPatterns(1, 0, z, abab)
            wave.assertPatterns(2, 0, z, baba)
            wave.assertPatterns(3, 0, z, aaaa, abab)
            wave.assertPatterns(4, 0, z, aaaa, abab, baba)
        }

        wave.observeStep()
        // aaba?
        // aaba?
        // aaba?
        // aaba?
        // aaba?
        for (z in 0..4) {
            assertEquals('a', wave[0, 0, z])
            assertEquals('a', wave[1, 0, z])
            assertEquals('b', wave[2, 0, z])
            assertEquals('a', wave[3, 0, z])
            assertEquals(null, wave[4, 0, z])
            wave.assertPatterns(0, 0, z, aaaa)
            wave.assertPatterns(1, 0, z, abab)
            wave.assertPatterns(2, 0, z, baba)
            wave.assertPatterns(3, 0, z, aaaa, abab)
            wave.assertPatterns(4, 0, z, aaaa, abab, baba)
        }
    }

    class Pattern1D(private val s: String) : WfcPattern<Char>(
        Array3D(s.length, 1, 1, s.toList().toTypedArray())
    ) {
        override fun toString(): String = s
    }

    class Pattern2D(private val s: String) : WfcPattern<Char>(
        Array3D(s.length/2, 1, s.length/2) { x, _, z ->
            s[z*s.length/2 + x]
        }
    ) {
        override fun toString(): String {
            val sb = StringBuilder()
            for (z in 0 until length) {
                if (z > 0) sb.append('\n')
                for (x in 0 until width)
                    sb.append(get(x, 0, z))
            }
            return sb.toString()
        }
    }
}

fun <S, P : IRandomOption> WfcModel<S, P, *>.assertPatterns(
    x: Int, y: Int, z: Int, vararg patterns: P
) {
    assertEquals(patterns.toSet(), getPossiblePatterns(x, y, z))
}