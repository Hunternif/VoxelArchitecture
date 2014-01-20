package hunternif.voxarch.plan.style;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.Structure;

/**
 * This style defines which exact block IDs will be used for the buildings. The
 * returned IDs will replace those within {@link Structure}s returned by
 * {@link Elements}. The returned {@link BlockData} may use metadata for
 * sub-blocks, but its orientation will be ignored. Every method returns a few
 * variants of the block, as each {@link Structure} can be made of multiple
 * materials.
 */
public interface Materials {
	BlockData[] floorBlocks();
	BlockData[] wallBlocks();
	BlockData[] ceilingBlocks();
	BlockData[] roofBlocks();
	/**
	 * Returns different kinds of blocks based on stair slope, i.e. regular
	 * stairs or half-slabs.
	 */
	BlockData[] stairBlocks(float slope);
	BlockData[] gateBlocks();
	BlockData[] decorationBlocks();
}
