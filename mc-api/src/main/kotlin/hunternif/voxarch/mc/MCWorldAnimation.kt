package hunternif.voxarch.mc

import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IBlockStorage
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.math.ceil

class MCWorldAnimation(
    private val world: IBlockStorage,
    private val blocksPerSecond: Int = 20,
    private val eventBus: EventBus = FMLCommonHandler.instance().bus()
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
    fun onTick(event: TickEvent.WorldTickEvent) {
        val blocksPerTick = blocksPerSecond.toDouble() / 20.0
        val intBlocksPerTick = ceil(blocksPerTick).toInt()
        val ticksPerBlock = 1.0 / blocksPerTick
        val intTicksPerBlock = ceil(ticksPerBlock).toInt()
        if (event.world.worldTime % intTicksPerBlock == 0L) {
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