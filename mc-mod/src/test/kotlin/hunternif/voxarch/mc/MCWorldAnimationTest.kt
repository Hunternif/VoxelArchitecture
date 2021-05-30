package hunternif.voxarch.mc

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IBlockStorage
import net.minecraftforge.eventbus.api.IEventBus
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MCWorldAnimationTest {
    @Mock lateinit var eventBus: IEventBus

    @Mock lateinit var world: IBlockStorage

    @Test
    fun `unregister after animation`() {
        val animation = MCWorldAnimation(world, 20, eventBus)
        for (b in 1..10) { animation.setBlock(0, 0, 0, block) }

        for (t in 1..12) {
            animation.onTick(t.toLong())
        }

        verify(world, times(10)).setBlock(0, 0, 0, block)
        verify(eventBus, times(2)).unregister(animation)
    }

    @Test
    fun `1 bps`() {
        val animation = MCWorldAnimation(world, 1, eventBus)
        for (b in 1..10) { animation.setBlock(0, 0, 0, block) }

        for (t in 1..40) {
            animation.onTick(t.toLong())
        }

        verify(world, times(2)).setBlock(0, 0, 0, block)
        verify(eventBus, never()).unregister(animation)
    }

    @Test
    fun `2 bps`() {
        val animation = MCWorldAnimation(world, 2, eventBus)
        for (x in 1..10) { animation.setBlock(0, 0, 0, block) }

        for (t in 1..40) {
            animation.onTick(t.toLong())
        }

        verify(world, times(4)).setBlock(0, 0, 0, block)
        verify(eventBus, never()).unregister(animation)
    }

    @Test
    fun `20 bps`() {
        val animation = MCWorldAnimation(world, 20, eventBus)
        for (x in 1..50) { animation.setBlock(0, 0, 0, block) }

        for (t in 1..40) {
            animation.onTick(t.toLong())
        }

        verify(world, times(40)).setBlock(0, 0, 0, block)
        verify(eventBus, never()).unregister(animation)
    }

    @Test
    fun `40 bps`() {
        val animation = MCWorldAnimation(world, 40, eventBus)
        for (x in 1..30) { animation.setBlock(0, 0, 0, block) }

        for (t in 1..10) {
            animation.onTick(t.toLong())
            verify(world, times(2*t)).setBlock(0, 0, 0, block)
        }
        verify(eventBus, never()).unregister(animation)
    }

    companion object {
        private val block = BlockData("B")
    }
}