package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.util.Direction3D
import org.junit.Assert.*
import org.junit.Test

class WfcObserveTest {
    /**
     * ```
     * Y
     *  +------> X
     *  | ad
     *  | a?
     *  V
     *  Z
     * ```
     */
    @Test
    fun contradiction() {
        val d = PixelTile("d")
        val wave = WfcTiledModel(2, 1, 2, listOf(a, d))
        wave[0, 0, 0] = a
        wave[0, 0, 1] = a
        wave[1, 0, 0] = d
        assertEquals(3, wave.collapsedCount)

        wave.propagate()

        assertEquals(3, wave.collapsedCount)
        wave.assertState(1, 0, 1)
        wave.observeStep()
        assertFalse(wave.isCollapsed)
        assertTrue(wave.isContradicted)
        assertEquals(null, wave[1, 0, 1])
    }

    /**
     * ```
     * Y
     *  +------> X
     *  | ac
     *  | a?
     *  V
     *  Z
     * ```
     */
    @Test
    fun `propagate collapses slot with 1 state in 2D grid`() {
        val wave = WfcTiledModel(2, 1, 2, listOf(a, b, c))
        wave[0, 0, 0] = a
        wave[0, 0, 1] = a
        wave[1, 0, 0] = c
        assertEquals(3, wave.collapsedCount)

        wave.propagate()

        assertEquals(4, wave.collapsedCount)
        assertTrue(wave.isCollapsed)
        assertFalse(wave.isContradicted)
        assertEquals(b, wave[1, 0, 1])
    }


    /**
     * ```
     * Y
     *  +------> X
     *  | abb
     *  | ???
     *  | c??
     *  V
     *  Z
     * ```
     */
    @Test
    fun `collapse lowest entropy point in 2D grid`() {
        val wave = WfcTiledModel(3, 1, 3, listOf(a, b, c), 0)
        wave[0, 0, 0] = a
        wave[1, 0, 0] = b
        wave[2, 0, 0] = b
        wave[0, 0, 2] = c
        assertEquals(4, wave.collapsedCount)

        wave.propagate()
        // propagate will collapse points with 1 possible state
        // abb
        // b??
        // c??
        assertEquals(5, wave.collapsedCount)
        wave.assertState(0, 0, 1, b)
        wave.assertState(1, 0, 1, a, b, c)
        wave.assertState(2, 0, 1, a, b, c)
        wave.assertState(1, 0, 2, b, c)
        wave.assertState(2, 0, 2, a, b, c)

        wave.observeStep()
        // abb
        // b??
        // cc?
        assertEquals(6, wave.collapsedCount)
        wave.assertState(1, 0, 2, c) // seed=0 leads to this
        wave.assertState(1, 0, 1, b, c)
        wave.assertState(2, 0, 1, a, b, c)
        wave.assertState(2, 0, 2, b, c)
        assertFalse(wave.isCollapsed)
        assertFalse(wave.isContradicted)
    }

    /**
     * ```
     * Y
     *  +-------> X
     *  | aa  ??
     *  | ??  ?c
     *  V
     *  Z
     * ```
     */
    @Test
    fun `propagate collapses slot in 3D grid`() {
        val wave = WfcTiledModel(2, 2, 2, listOf(a, b, c), 0)
        wave[0, 0, 0] = a
        wave[1, 0, 0] = a
        wave[1, 1, 1] = c
        assertEquals(3, wave.collapsedCount)

        wave.propagate()
        // propagate will collapse points with 1 possible state
        // aa  ?b
        // ?b  ?c
        assertEquals(5, wave.collapsedCount)
        wave.assertState(0, 0, 1, a, b)
        wave.assertState(1, 0, 1, b)
        wave.assertState(0, 1, 0, a, b)
        wave.assertState(1, 1, 0, b)
        wave.assertState(0, 1, 1, b, c)
        assertFalse(wave.isCollapsed)
        assertFalse(wave.isContradicted)
    }

    /**
     * ```
     * Y
     *  +---------------> X
     *  | a??  ???  ???
     *  | ???  ???  ???
     *  | ???  ???  ???
     *  V
     *  Z
     * ```
     */
    @Test
    fun `propagate back & forth in 3D grid`() {
        val wave = WfcTiledModel(3,3, 3, listOf(a, b, c))

        wave[0, 0, 0] = a
        wave[1, 0, 0] = null // this does nothing
        wave.propagate()
        assertEquals(1, wave.collapsedCount)
        wave.assertState(0, 0, 0, a)
        wave.assertState(1, 0, 0, a, b)
        wave.assertState(2, 0, 0, a, b, c)
        wave.assertState(0, 0, 1, a, b)
        wave.assertState(0, 0, 2, a, b, c)
        wave.assertState(0, 1, 0, a, b)
        wave.assertState(0, 2, 0, a, b, c)
        wave.assertState(0, 1, 1, a, b, c)
        wave.assertState(1, 0, 1, a, b, c)
        wave.assertState(1, 1, 0, a, b, c)
        wave.assertState(1, 1, 1, a, b, c)
        wave.assertState(2, 2, 2, a, b, c)

        wave[0, 0, 0] = a // this does nothing
        wave[1, 0, 0] = a
        wave[2, 0, 0] = null // this does nothing
        // aa?  ???  ???
        // ???  ???  ???
        // ???  ???  ???
        wave.propagate()
        assertEquals(2, wave.collapsedCount)
        wave.assertState(0, 0, 0, a)
        wave.assertState(1, 0, 0, a)
        wave.assertState(2, 0, 0, a, b)
        wave.assertState(0, 0, 1, a, b)
        wave.assertState(0, 0, 2, a, b, c)
        wave.assertState(0, 1, 0, a, b)
        wave.assertState(0, 2, 0, a, b, c)
        wave.assertState(0, 1, 1, a, b, c)
        wave.assertState(1, 0, 1, a, b)
        wave.assertState(1, 1, 0, a, b)
        wave.assertState(1, 1, 1, a, b, c)
        wave.assertState(2, 2, 2, a, b, c)

        wave[0, 0, 0] = null
        // ?a?  ???  ???
        // ???  ???  ???
        // ???  ???  ???
        wave.propagate()
        assertEquals(1, wave.collapsedCount)
        wave.assertState(0, 0, 0, a, b)
        wave.assertState(1, 0, 0, a)
        wave.assertState(2, 0, 0, a, b)
        wave.assertState(0, 0, 1, a, b, c)
        wave.assertState(0, 0, 2, a, b, c)
        wave.assertState(0, 1, 0, a, b, c)
        wave.assertState(0, 2, 0, a, b, c)
        wave.assertState(0, 1, 1, a, b, c)
        wave.assertState(1, 0, 1, a, b)
        wave.assertState(1, 1, 0, a, b)
        wave.assertState(1, 1, 1, a, b, c)
        wave.assertState(2, 2, 2, a, b, c)
    }

    class PixelTile(private val name: String): WfcTile {
        override val probability: Double = 1.0
        override fun matchesSide(other: WfcTile, dir: Direction3D) =
            matchMap[this]?.contains(other) ?: false
        override fun toString() = name
    }

    companion object {
        private val a = PixelTile("a")
        private val b = PixelTile("b")
        private val c = PixelTile("c")

        private val matchMap = mapOf(
            a to listOf(a, b),
            b to listOf(a, b, c),
            c to listOf(b, c)
        )
    }
}

fun <T: WfcTile> WfcTiledModel<T>.assertState(x: Int, y: Int, z: Int, vararg states: T) {
    assertEquals(states.toSet(), getPossibleStates(x, y, z))
}