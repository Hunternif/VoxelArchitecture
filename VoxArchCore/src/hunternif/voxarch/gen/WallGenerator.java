package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.IBlockStorage;

public interface WallGenerator {
	void generateWall(IBlockStorage dest, Wall wall, Materials materials);
}
