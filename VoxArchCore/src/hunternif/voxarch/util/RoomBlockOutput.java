package hunternif.voxarch.util;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;

/**
 * This block storage that only allows modifying blocks that are within the
 * walls of the specified room. Used for clearing volume for rooms and when
 * building floor and ceiling.
 * @author Hunternif
 */
public class RoomBlockOutput implements IBlockStorage {
	private final IBlockStorage storage;
	private final Room room;

	public RoomBlockOutput(IBlockStorage storage, Room room) {
		this.storage = storage;
		this.room = room;
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		return storage.getBlock(x, y, z);
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		if (!isWithinRoom(x, y, z)) return;
		storage.setBlock(x, y, z, block);
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		if (!isWithinRoom(x, y, z)) return;
		storage.clearBlock(x, y, z);
	}
	
	public boolean isWithinRoom(int x, int y, int z) {
		//TODO check if the block is within the room's walls
		return false;
	}

}
