package hunternif.voxarch.gen;

import hunternif.voxarch.storage.BlockData;

/**
 * These methods return sets of block data to use when generating their
 * respective elements. For example, different blocks can be used to make
 * an ornament.
 * @author Hunternif
 */
public interface Materials {
	BlockData[] floorBlocks();
	BlockData[] ceilingBlocks();
	BlockData[] wallBlocks();
	BlockData[] gateBlocks();
	/**
     * Returns different kinds of blocks based on stair slope, i.e. regular
     * stairs or half-slabs.
     */
	BlockData[] stairsBlocks(double slope);
	BlockData[] decorationBlocks();
	//TODO: add sources of light, i.e. tourches, glowing blocks, fire
}
