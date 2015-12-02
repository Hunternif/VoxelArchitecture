package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Extended Minecraft-specific block data. Use this class to paste spawn things
 * like paintings, item frames, mob spawners etc.
 * <p>
 * This block data type is not persistent, meaning that it can only be retrieved
 * back from the world if it was previously set in without restarting the JVM.
 * </p>
 * @author Hunternif
 */
public class ExtBlockDataMC extends BlockData {

	public ExtBlockDataMC(Block block) {
		this(block, 0);
	}
	public ExtBlockDataMC(Block block, int metadata) {
		this(Block.getIdFromBlock(block), metadata);
	}
	public ExtBlockDataMC(int id, int metadata) {
		super(id, metadata);
	}
	
	public Block getBlock() {
		return Block.getBlockById(getId());
	}
	
	/** Called from MCWorld.setBlock(). Use this for special processing like
	 * spawning HangingEntities, chests etc. */
	public void onPasteIntoWorld(World world, int x, int y, int z) {}
	
}
