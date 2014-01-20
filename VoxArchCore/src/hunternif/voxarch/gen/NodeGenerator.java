package hunternif.voxarch.gen;

import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Node;
import hunternif.voxarch.plan.style.Style;
import hunternif.voxarch.storage.IBlockStorage;

/**
 * A generator converts an abstract node into concrete blocks in a voxel storage
 * with respect to architectural style.
 * @param <N> the type of node.
 * @author Hunternif
 */
public interface NodeGenerator<N extends Node> {
	/**
	 * Generate the node into the voxel storage, the coordinates designating
	 * origin of the whole {@link ArchPlan}.
	 * @param world	storage to generate into.
	 * @param x
	 * @param y
	 * @param z
	 * @param node	node to generate.
	 * @param style	elementary structures, geometry, materials.
	 */
	void generate(IBlockStorage world, int x, int y, int z, N node, Style style);
}
