package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.util.IRandomOption
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.wfc.WfcModel
import org.junit.Assert.*
import org.junit.Test

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
        // ?a?? -> ?aaa
        assertEquals(null, wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('a', wave[2, 0, 0])
        assertEquals('a', wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa, bb) // not propagated yet
        wave.assertPatterns(1, 0, 0, aa)
        wave.assertPatterns(2, 0, 0, aa)
        wave.assertPatterns(3, 0, 0, aa)

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
        wave.assertPatterns(0, 0, 0, aa, ab, bb) // not propagated yet
        wave.assertPatterns(1, 0, 0, aa, ab)
        wave.assertPatterns(2, 0, 0, aa, ab, bb)
        wave.assertPatterns(3, 0, 0, aa, ab, bb)

        wave[2, 0, 0] = 'b'
        // ?ab? -> ?abb
        assertEquals(null, wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('b', wave[2, 0, 0])
        assertEquals('b', wave[3, 0, 0])
        wave.assertPatterns(0, 0, 0, aa, ab, bb) // not propagated yet
        wave.assertPatterns(1, 0, 0, aa, ab) // not propagated yet
        wave.assertPatterns(2, 0, 0, bb)
        wave.assertPatterns(3, 0, 0, bb)

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
    fun `observe 1d wave with single long pattern`() {
        val bab = Pattern1D("bab")
        val wave = WfcOverlapModel(5, 1, 1, listOf(bab))

        wave.observeStep()
        // bab??
        assertEquals('b', wave[0, 0, 0])
        assertEquals('a', wave[1, 0, 0])
        assertEquals('b', wave[2, 0, 0])
        assertEquals('a', wave[3, 0, 0])
        assertEquals('b', wave[4, 0, 0])
        wave.assertPatterns(0, 0, 0, bab)
        wave.assertPatterns(1, 0, 0)
        wave.assertPatterns(2, 0, 0, bab)
        wave.assertPatterns(3, 0, 0)
        wave.assertPatterns(4, 0, 0, bab)
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

    class Pattern1D(private val s: String) : WfcPattern<Char>(
        Array3D(s.length, 1, 1, s.toList().toTypedArray())
    ) {
        override fun toString(): String = s
    }
}

fun <S, P : IRandomOption> WfcModel<S, P, *>.assertPatterns(
    x: Int, y: Int, z: Int, vararg patterns: P
) {
    assertEquals(patterns.toSet(), getPossiblePatterns(x, y, z))
}