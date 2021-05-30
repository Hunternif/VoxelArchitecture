package hunternif.voxarch.mc

import com.google.common.annotations.VisibleForTesting
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IBlockStorage
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.*
import kotlin.math.ceil

class MCWorldAnimation(
    private val world: IBlockStorage,
    private val blocksPerSecond: Int = 20,
    private val eventBus: IEventBus = MinecraftForge.EVENT_BUS
) : IBlockStorage by world {
    private data class BlockEntry(
        val x: Int,
        val y: Int,
        val z: Int,
        val block: BlockData?
    )

    private val queue = LinkedList<BlockEntry>()

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        queue.addLast(BlockEntry(x, y, z, block))
    }

    @SubscribeEvent
    fun onTickEvent(event: TickEvent.WorldTickEvent) {
        onTick(event.world.gameTime)
    }

    @VisibleForTesting
    internal fun onTick(ticksElapsed: Long) {
        val blocksPerTick = blocksPerSecond.toDouble() / 20.0
        val intBlocksPerTick = ceil(blocksPerTick).toInt()
        val ticksPerBlock = 1.0 / blocksPerTick
        val intTicksPerBlock = ceil(ticksPerBlock).toInt()
        if (ticksElapsed % intTicksPerBlock == 0L) {
            if (!queue.isEmpty()) {
                for (t in 1..intBlocksPerTick) {
                    queue.pop().apply {
                        world.setBlock(x, y, z, block)
                    }
                    if (queue.isEmpty()) break
                }
            } else {
                println("Building animation complete")
                eventBus.unregister(this)
            }
        }
    }
}