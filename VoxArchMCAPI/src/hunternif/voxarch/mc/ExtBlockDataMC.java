package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import net.minecraft.world.World;

/**
 * Extended Minecraft-specific block data. Use this class to paste spawn things
 * like paintings, item frames, mob spawners etc.
 * @author Hunternif
 */
public abstract class ExtBlockDataMC extends BlockData {

	public ExtBlockDataMC(int id, int metadata) {
		super(id, metadata);
	}
	
	/** Called from MCWorld.setBlock(). Use this for special processing like
	 * spawning HangingEntities, chests etc. */
	public abstract void onPasteIntoWorld(World world, int x, int y, int z);
	
}
