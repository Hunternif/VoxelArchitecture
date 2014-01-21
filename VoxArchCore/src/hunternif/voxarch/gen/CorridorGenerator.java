package hunternif.voxarch.gen;

import hunternif.voxarch.plan.NodeCorridor;
import hunternif.voxarch.plan.style.Style;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.PositionTransformer;

/**
 * Generates a straight corridor. The corridor is assumed to have been connected
 * to non-null nodes at both ends. If the corridor has not been axis-aligned, it
 * will nevertheless be rotated at the correct angle, which will cause aliasing.
 * @author Hunternif
 */
public class CorridorGenerator implements NodeGenerator<NodeCorridor> {

	@Override
	public void generate(IBlockStorage world, int x, int y, int z,
			NodeCorridor node, Style style) {
		double angle = Math.atan2(
				node.getEnd().getOrigin().z - node.getStart().getOrigin().z,
				node.getEnd().getOrigin().x - node.getStart().getOrigin().x);
		PositionTransformer trans = new PositionTransformer(world).translation(x, y, z).rotationY(angle);
		//TODO paste elements through transformation and repetitively rotate it
		// 90 degrees to switch to other walls.
	}

}
