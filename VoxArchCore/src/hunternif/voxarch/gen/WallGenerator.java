package hunternif.voxarch.gen;

import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.IBlockStorage;

public interface WallGenerator {
	/** The wall runs along the X axis. */
	void generateWall(IBlockStorage dest, Wall wall, Materials materials);
}
