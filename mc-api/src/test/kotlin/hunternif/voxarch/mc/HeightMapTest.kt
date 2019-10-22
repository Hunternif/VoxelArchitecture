package hunternif.voxarch.mc

import com.nhaarman.mockitokotlin2.*
import hunternif.voxarch.vector.IntVec2
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import hunternif.voxarch.mc.HeightMap.Companion.heightMap
import org.junit.Assert.assertEquals

@RunWith(MockitoJUnitRunner::class)
class HeightMapTest {
    @Mock lateinit var world: World

    @Before
    fun setup() {
        whenever(world.getHeight(any())) doReturn BlockPos(0, 0, 0)
    }

    @Test
    fun `scan area`() {
        world.heightMap(IntVec2(3, 3), IntVec2(3, 4))

        verify(world, never()).getHeight(argThat { x < 2 || z < 2 })
        verify(world, times(12)).getHeight(argThat { x >= 2 || x <= 4 })
        verify(world, times(12)).getHeight(argThat { z >= 2 || z <= 5 })
        verify(world, never()).getHeight(argThat { x > 4 || z > 5 })
    }

    @Test
    fun getColor() {
        val heightMap = HeightMap(0, 0).apply {
            minHeight = 0
            maxHeight = 2
        }
        assertEquals(0x000000, heightMap.getColor(0))
        assertEquals(0x657f65, heightMap.getColor(1))
        assertEquals(0xccffcc, heightMap.getColor(2))
    }
}