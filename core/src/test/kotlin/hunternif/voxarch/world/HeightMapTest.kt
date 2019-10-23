package hunternif.voxarch.world

import com.nhaarman.mockitokotlin2.*
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.HeightMap.Companion.heightMap
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.intThat
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HeightMapTest {
    @Mock lateinit var world: IBlockWorld

    @Before
    fun setup() {
        whenever(world.getHeight(any(), any())) doReturn 0
    }

    @Test
    fun `scan area`() {
        world.heightMap(IntVec2(3, 3), IntVec2(3, 4))

        verify(world, never()).getHeight(intThat { x -> x !in 2..4 }, any())
        verify(world, never()).getHeight(any(), intThat { z -> z !in 2..5 })
        verify(world, times(12)).getHeight(
            intThat { x -> x in 2..4 }, intThat { z -> z in 2..5 })
    }
}