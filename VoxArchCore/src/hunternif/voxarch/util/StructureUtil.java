package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.IStorageFactory;
import hunternif.voxarch.storage.Structure;

public class StructureUtil {
	/**
	 * Creates a new structure with the content of the specified structure
	 * rotated at an arbitrary angle around the Y axis. <b>Warning:</b>
	 * Rotating at a non-right angle will screw the structure significantly.
	 * @param factory	is needed to allocate memory for the new structure.
	 * @param toRotate	initial structure to rotate. It is not modified.
	 * @param angle		is in degrees, counterclockwise.
	 * @param closeGaps	attempt to leave no holes caused by aliasing.
	 */
	public static Structure rotate(IStorageFactory factory, Structure toRotate, float angle, boolean closeGaps) {
		angle = MathUtil.clampAngle(angle);
		/*
		 * Aerial view of the reference frame:
		 * Y
		 *  +----> X (East)
		 *  |
		 *  V
		 *  Z
		 */
		// Minus angle, because when rotating against the Y axis
		// counterclockwise the reference frame XZY is left-handed.
		Matrix2 rot = Matrix2.rotationMatrix(-angle);
		// This rotation matrix also serves as the new basis.
		
		int width = toRotate.getStorage().getWidth();
		int height = toRotate.getStorage().getHeight();
		int length = toRotate.getStorage().getLength();
		
		// Going to find the origin point for the storage (not the structure!),
		// i.e. the top left corner (see the figure above).
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
		
		// Using vector of doubles because precision will be needed for
		// in-between transformations.
		Vec2 blockCoords = new Vec2(0, 0);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					BlockData block = toRotate.getStorage().getBlock(x, y, z);
					if (block == null) continue;
					block.rotate(angle);
					// Add 0.5 to approximate position of the center of the block:
					blockCoords.set(x + 0.5, z + 0.5);
					rot.multiply(blockCoords).subtract(storageOrigin);
					newStorage.setBlock((int)blockCoords.x, y, (int)blockCoords.y, block);
					// Close any gaps:
					if (closeGaps) {
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
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					struct.getStorage().setBlock(x, y, z, block);
				}
			}
		}
		return struct;
	}
}
