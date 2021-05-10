package hunternif.voxarch.mc

import hunternif.voxarch.storage.BlockData
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

class MCWorldAnimation(world: World) : MCWorld(world) {
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
        if (event.world.worldTime % 2 == 0L) { // once every 2 ticks
            if (!queue.isEmpty()) {
                queue.pop().apply {
                    super.setBlock(x, y, z, block)
                }
            } else {
                println("Building animation complete")
                FMLCommonHandler.instance().bus().unregister(this)
            }
        }
    }
}