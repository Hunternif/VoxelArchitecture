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
 * <p> The purpose of this class is to preserve only the <b>shape</b> of the
 * room, defined by its walls.
 * Combine it with {@link PositionTransformer} to account for its position
 * and rotation.</p>
 * <p><b>Coordinates are set from the corner of the room, not origin.</b></B></p>
 * TODO: use coordinates start from room origin.
 * @author Hunternif
 */
public class RoomConstrainedStorage implements IFixedBlockStorage {
	private final IBlockStorage storage;
	private final Room room;
	
	/** Offset from the walls outside. **/
	private double offset = 0;
	
	/** Map of 3D vectors pointing from each wall's P1 to is P2. Reused for
	 * finding cross product. */
	private final Map<Wall, Vec3> wallVectorMap = new HashMap<>();
	
	/** Map of 3D vector normals for each wall. Reused for calculating offset. */
	private final Map<Wall, Vec3> wallNormalMap = new HashMap<>();

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
		if (!isWithinRoom(x, y, z)) return;
		storage.setBlock(x, y, z, block);
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		if (!isWithinRoom(x, y, z)) return;
		//TODO: BUG: When generating a 16x16 room with 16 round walls,
		// one block in the corner is not cleared.
		storage.clearBlock(x, y, z);
	}
	
	public void setOffset(double offset) {
		this.offset = offset;
	}

	public boolean isWithinRoom(Vec3 vec) {
		return isWithinRoom(vec.x, vec.y, vec.z);
	}

	/** Returns true if the specified point is within the volume of this room.
	 * The coordinates are relative to the corner of the room. */
	public boolean isWithinRoom(double x, double y, double z) {
		// Check if the point is within the room's bounding box:
		if (y < 0 || y > room.getSize().y ||
			x < offset || x > room.getSize().x - offset ||
			z < offset || z > room.getSize().z - offset) {
			return false;
		}
		// Check if the point is within the walls:
		for (Wall wall : room.getWalls()) {
			Vec3 wallNorm = getWallNormal(wall);
			Vec3 point = new Vec3(x - room.getSize().x/2 - wall.getP1().x + wallNorm.x*offset,
								  0,
								  z - room.getSize().z/2 - wall.getP1().y + wallNorm.z*offset);
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
	
	/** Returns a normal to the wall's vector and puts is in the map for reuse.
	 * The returned vector is guaranteed to be non-null for non-null walls. */
	private Vec3 getWallNormal(Wall wall) {
		Vec3 vec = wallNormalMap.get(wall);
		if (vec == null) {
			vec = getWallVector(wall).crossProduct(Vec3.UNIT_Y).normalizeLocal();
			wallNormalMap.put(wall, vec);
		}
		return vec;
	}

	@Override
	public int getWidth() {
		// + 1 because a room occupies size + 1 blocks.
		return (int)room.getSize().x + 1;
	}

	@Override
	public int getHeight() {
		// + 1 because a room occupies size + 1 blocks.
		return (int)room.getSize().y + 1;
	}

	@Override
	public int getLength() {
		// + 1 because a room occupies size + 1 blocks.
		return (int)room.getSize().z + 1;
	}

}
