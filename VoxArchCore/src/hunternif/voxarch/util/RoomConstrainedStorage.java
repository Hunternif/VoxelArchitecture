package hunternif.voxarch.util;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.vector.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * This block storage that only allows modifying blocks that are within the
 * walls of the specified room. Used for clearing volume for rooms and when
 * building the floor and the ceiling.
 * @author Hunternif
 */
public class RoomConstrainedStorage implements IFixedBlockStorage {
	private final IBlockStorage storage;
	private final Room room;
	
	/** Map of 3D vectors pointing from each wall's P1 to is P2. Reused for
	 * finding cross product. */
	private final Map<Wall, Vec3> wallVectorMap = new HashMap<Wall, Vec3>();

	public RoomConstrainedStorage(IBlockStorage storage, Room room) {
		this.storage = storage;
		this.room = room;
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		return storage.getBlock(x, y, z);
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		if (!isWithinRoom(x + 0.5, y, z + 0.5)) return;
		storage.setBlock(x, y, z, block);
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		if (!isWithinRoom(x + 0.5, y, z + 0.5)) return;
		storage.clearBlock(x, y, z);
	}
	
	/** Returns true if the specified point is within the volume of this room.
	 * When checking block coordinates, use the center of the block instead of
	 * its corner. */
	//TODO: check more rigorously, there is a bug here!
	public boolean isWithinRoom(double x, double y, double z) {
		// Check if the point is above the floor and below the ceiling:
		if (y < room.getOrigin().y || y > room.getOrigin().y + room.getSize().y) {
			return false;
		}
		// Check if the point is within the walls:
		for (Wall wall : room.getWalls()) {
			// The supplied (x, y, z) are absolute, but wall coordinates are
			// relative to the room's origin. Keeping that in mind, find the
			// vector from the wall's P1 to the point in question:
			Vec3 point = new Vec3(x - room.getOrigin().x - wall.getP1().x, 0,
								  z - room.getOrigin().z - wall.getP1().y);
			// If the point is inside the room, then the cross product of the
			// wall vector with it will point upwards.
			Vec3 wallVec = getWallVector(wall);
			if (wallVec.crossProduct(point).y < 0) {
				return false;
			}
		}
		return true;
	}
	
	/** Returns a vector from P1 to P2 and puts is in the map for reuse. The
	 * returned vector is guaranteed to be non-null for non-null walls. */
	private Vec3 getWallVector(Wall wall) {
		Vec3 vec = wallVectorMap.get(wall);
		if (vec == null) {
			vec = new Vec3(wall.getP2().x - wall.getP1().x, 0,
						   wall.getP2().y - wall.getP1().y);
			wallVectorMap.put(wall, vec);
		}
		return vec;
	}

	@Override
	public int getWidth() {
		return (int)room.getSize().x;
	}

	@Override
	public int getHeight() {
		return (int)room.getSize().y;
	}

	@Override
	public int getLength() {
		return (int)room.getSize().z;
	}

}
