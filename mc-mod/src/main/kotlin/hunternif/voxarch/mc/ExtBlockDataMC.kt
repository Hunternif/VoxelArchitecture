package hunternif.voxarch.mc

import hunternif.voxarch.storage.BlockData
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraftforge.registries.ForgeRegistries

/**
 * Extended Minecraft-specific block data. Use this class to spawn things
 * like paintings, item frames, mob spawners etc.
 *
 *
 * This block data type is not persistent, meaning that it can only be retrieved
 * back from the world if it was previously set in without restarting the JVM.
 *
 * @author Hunternif
 */
class ExtBlockDataMC(val block: Block) : BlockData(block.key) {

    private val resource = ForgeRegistries.BLOCKS.getKey(block)!!

    /** Called from MCWorld.setBlock(). Use this for special processing like
     * spawning HangingEntities, chests etc.  */
    fun onPasteIntoWorld(world: World?, x: Int, y: Int, z: Int) {}
}