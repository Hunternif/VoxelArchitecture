package hunternif.voxarch.sandbox;

import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.gen.impl.SimpleWallGenerator;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.BlockOrientation;
import hunternif.voxarch.util.MathUtil;

public class SimpleTorchlitWallGen extends SimpleWallGenerator {
	public int torchWallSpacing = 4;
	public int torchCeilSpacing = 1;
	
	@Override
	public void generateWall(IBlockStorage dest, Wall wall, Materials materials) {
		super.generateWall(dest, wall, materials);
		// Add torches:
		BlockData block = materials.oneBlockProp("torch");
		if (block == null) return;
		// Set orientation to face inward:
		block.setOrientaion(BlockOrientation.NORTH);
		//TODO: some torches fall down. Consider spawning them as props.
		// Starting with a half-step from the edge, and not including the edge
		// itself because it will probably be covered by another wall:
		for (int x = torchWallSpacing / 2; x < wall.getLength(); x += torchWallSpacing) {
			dest.setBlock(x, MathUtil.floor(wall.getHeight() - torchCeilSpacing), -1, block);
		}
		/*
		// As an example, let's do it by adding props:
		Vec2 step = wall.getP2().subtract(wall.getP1()).normalizeLocal().multiplyLocal(torchWallSpacing);
		// Start at half-step from the edge of the wall:
		Vec3 torchPos = new Vec3(wall.getP1().x + step.x/2,
								wall.getHeight() - torchCeilSpacing,
								wall.getP1().y + step.y/2);
		for (int x = torchWallSpacing/2; x <= wall.getLength(); x++) {
			wall.getRoom().addProp("torch", torchPos, 0);
			torchPos.add(step.x, 0, step.y);
		}
		*/
	}
}
