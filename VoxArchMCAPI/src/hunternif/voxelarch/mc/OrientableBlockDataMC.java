package hunternif.voxelarch.mc;

import hunternif.voxarch.util.BlockOrientation;
import hunternif.voxarch.util.OrientableBlockData;

public class OrientableBlockDataMC extends OrientableBlockData {

	public OrientableBlockDataMC(int id, int metadata) {
		super(id, metadata);
	}

	@Override
	public BlockOrientation getOrientaion() {
		switch (metadata) {
		case 1: return BlockOrientation.EAST;
		case 2: return BlockOrientation.WEST;
		case 3: return BlockOrientation.SOUTH;
		case 4: return BlockOrientation.NORTH;
		default: return BlockOrientation.NONE;
		}
	}

	@Override
	public void setOrientaion(BlockOrientation orient) {
		switch (orient) {
		case EAST: metadata = 1; break;
		case WEST: metadata = 2; break;
		case SOUTH: metadata = 3; break;
		case NORTH: metadata = 4; break;
		default: break;
		}
	}

}
