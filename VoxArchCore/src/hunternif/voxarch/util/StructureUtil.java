package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.IStorageFactory;
import hunternif.voxarch.storage.Structure;
import hunternif.voxarch.vector.IntVec3;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Vec2;

public class StructureUtil {
	/**
	 * Creates a new structure with the content of the specified structure
	 * rotated at an arbitrary angle around the Y axis. <b>Warning!
	 * Rotating at a non-right angle will screw the structure significantly.</b>
	 * @param factory	is needed to allocate memory for the new structure.
	 * @param toRotate	initial structure to rotate. It is not modified.
	 * @param angle		is in degrees, counterclockwise.
	 * @param closeGaps	attempt to leave no holes caused by aliasing.
	 */
	public static Structure rotate(IStorageFactory factory, Structure toRotate, double angle, boolean closeGaps) {
		angle = MathUtil.clampAngle(angle);
		/*
		 * Aerial view of the reference frame:
		 * Y
		 *  +----> X (East)
		 *  |
		 *  V
		 *  Z
		 */
		// Minus angle, because when rotating around the Y axis
		// counterclockwise the reference frame XZY is left-handed.
		Matrix2 rot = Matrix2.rotationMatrix(-angle);
		// This rotation matrix also serves as the new basis.
		
		int width = toRotate.getStorage().getWidth();
		int height = toRotate.getStorage().getHeight();
		int length = toRotate.getStorage().getLength();
		
		// Going to find the origin point for the storage (not the structure!),
		// i.e. the top left corner of the AABB of the rotated structure.
		Vec2 storageOrigin = new Vec2(0, 0);
		
		// Find storage origin coordinates as minimum of coordinates of 4 corner
		// points of the storage rectangle.
		Vec2 tl = rot.multiply(new Vec2(0, 0));
		Vec2 tr = rot.multiply(new Vec2(width, 0));
		Vec2 br = rot.multiply(new Vec2(width, length));
		Vec2 bl = rot.multiply(new Vec2(0, length));
		
		storageOrigin.x = MathUtil.min(tl.x, tr.x, br.x, bl.x);
		storageOrigin.y = MathUtil.min(tl.y, tr.y, br.y, bl.y);
		
		// Find the new dimensions of the storage:
		int newWidth = MathUtil.roundUp(Math.abs(rot.m00*width + rot.m01*length));
		int newLength = MathUtil.roundUp(Math.abs(-rot.m10*width + rot.m11*length));
		
		IFixedBlockStorage newStorage = factory.createFixed(newWidth, height, newLength);
		Structure newStruct = new Structure(newStorage);
		
		// Using a vector of doubles because precision will be needed for
		// in-between transformations.
		Vec2 blockCoords = new Vec2(0, 0);
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				// Add 0.5 to x and z to operate with the position of the center
				// of the block instead of its corner:
				blockCoords.set(x + 0.5, z + 0.5);
				rot.multiply(blockCoords).subtract(storageOrigin);
				for (int y = 0; y < height; y++) {
					BlockData block = toRotate.getStorage().getBlock(x, y, z).clone();
					if (block == null) continue;
					block.rotate(angle);
					newStorage.setBlock((int)blockCoords.x, y, (int)blockCoords.y, block);
					if (closeGaps) {
						// In order to close gaps, round coordinates instead of
						// simply truncating the mantissa when casting to int:
						int roundX = Math.min(MathUtil.roundDown(blockCoords.x), newWidth - 1);
						int roundZ = Math.min(MathUtil.roundDown(blockCoords.y), newLength - 1);
						newStorage.setBlock((int)blockCoords.x, y, roundZ, block);
						newStorage.setBlock(roundX, y, (int)blockCoords.y, block);
						newStorage.setBlock(roundX, y, roundZ, block);
					}
				}
			}
		}
		
		// Update the origin of the structure:
		IntVec3 origin = toRotate.getOrigin();
		Vec2 origin2D = new Vec2(origin.x + 0.5, origin.z + 0.5);
		rot.multiply(origin2D).subtract(storageOrigin);
		newStruct.setOrigin((int)origin2D.x, origin.y, (int)origin2D.y);
		
		return newStruct;
	}
	
	/** Creates a new structure that is a box of size (width, height, length)
	 * filled uniformly with the specified block data. */
	public static Structure createFilledBox(IStorageFactory factory, int width, int height, int length, BlockData block) {
		Structure struct = new Structure(factory.createFixed(width, height, length));
		fill(struct.getStorage(), block);
		return struct;
	}
	
	/** Fills the specified fixed-size block storage uniformly with given block. */
	public static void fill(IFixedBlockStorage storage, BlockData block) {
		for (int x = 0; x < storage.getWidth(); x++) {
			for (int y = 0; y < storage.getHeight(); y++) {
				for (int z = 0; z < storage.getLength(); z++) {
					storage.setBlock(x, y, z, block);
				}
			}
		}
	}
	
	/**
	 * Remove all blocks from the specified volume that the specified filter
	 * doesn't accept.
	 */
	public static void clearVolume(IBlockStorage storage, Region3 volume, IBlockFilter filter) {
		for (int x = volume.minX; x <= volume.maxX; x++) {
			for (int z = volume.minZ; z <= volume.maxZ; z++) {
				for (int y = volume.minY; y <= volume.maxY; y++) {
					BlockData block = storage.getBlock(x, y, z);
					if (!filter.accept(block)) {
						storage.clearBlock(x, y, z);
					}
				}
			}
		}
	}
	
	/**
	 * Remove all blocks from the specified fixed-size block storage.
	 */
	public static void clearStorage(IFixedBlockStorage storage) {
		for (int x = 0; x < storage.getWidth(); x++) {
			for (int y = 0; y < storage.getHeight(); y++) {
				for (int z = 0; z < storage.getLength(); z++) {
					storage.clearBlock(x, y, z);
				}
			}
		}
	}
	
	/**
	 * Copy and paste blocks from one storage into another at the specified
	 * coordinates.
	 */
	public static void pasteStructure(IBlockStorage toStorage, IFixedBlockStorage fromStorage, int x, int y, int z) {
		for (int nx = 0; nx < fromStorage.getWidth(); nx++) {
			for (int ny = 0; ny < fromStorage.getHeight(); ny++) {
				for (int nz = 0; nz < fromStorage.getLength(); nz++) {
					toStorage.setBlock(x + nx, y + ny, z + nz, fromStorage.getBlock(nx, ny, nz));
				}
			}
		}
	}
	
	/**
	 * Paste the specified structure into the specified storage so that the
	 * structure's origin point is at the specified coordinates.
	 */
	public static void pasteStructure(IBlockStorage toStorage, Structure structure, int x, int y, int z) {
		pasteStructure(toStorage, structure.getStorage(),
				x - structure.getOrigin().x,
				y - structure.getOrigin().y,
				z - structure.getOrigin().z);
	}
}
