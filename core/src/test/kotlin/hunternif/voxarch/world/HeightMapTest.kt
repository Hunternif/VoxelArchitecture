package hunternif.voxarch.world

import com.nhaarman.mockitokotlin2.*
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.HeightMap.Companion.heightMap
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.intThat
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HeightMapTest {
    @Mock lateinit var world: IBlockWorld

    @Test
    fun `scan area`() {
        world.heightMap(IntVec2(3, 3), IntVec2(3, 4))

        verify(world, never()).getHeight(intThat { x -> x !in 2..4 }, any())
        verify(world, never()).getHeight(any(), intThat { z -> z !in 2..5 })
        verify(world, times(12)).getHeight(
            intThat { x -> x in 2..4 }, intThat { z -> z in 2..5 })
    }

    @Test
    fun `clip height`() {
        HeightMap(1, 1).apply {
            this[0, 0] = 5

            minHeight = 3
            Assert.assertEquals(5, get(0, 0))

            minHeight = 6
            Assert.assertEquals(6, get(0, 0))

            minHeight = 0
            maxHeight = 4
            Assert.assertEquals(4, get(0, 0))
        }
    }
}