package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;

/**
 * A shell over storage, helper class for transforming block coordinates.
 * Successively applies rotation and translation.
 * @author Hunternif
 */
public class PositionTransformer implements IBlockStorage {
	private final IBlockStorage storage;
	/** Translation coordinates. */
	private final double tx, ty, tz;
	/** Angle of rotation. */
	private final double angle;
	private final boolean closeGaps;
	
	private final Matrix2 rot;
	
	// Saving memory by reusing the same vector:
	private final Vec2 vec2 = new Vec2(0, 0);
	private final Vec3 vec3 = new Vec3(0, 0, 0);
	
	/**
	 * @param storage the underlying storage.
	 * @param x translation.
	 * @param y translation.
	 * @param z translation.
	 * @param angle angle of rotation, counterclockwise.
	 * @param closeGaps if true, will apply every operation to the whole area
	 * 					that the rotated block covers thereby eliminating gaps
	 * 					caused by aliasing when rotating at a non-right angle.
	 */
	public PositionTransformer(IBlockStorage storage, double x, double y, double z, double angle, boolean closeGaps) {
		super();
		this.storage = storage;
		this.tx = x;
		this.ty = y;
		this.tz = z;
		this.angle = angle;
		this.closeGaps = closeGaps;
		// Minus angle, because when rotating around the Y axis
		// counterclockwise the reference frame XZY is left-handed.
		this.rot = Matrix2.rotationMatrix(-angle);
	}
	
	/** No translation, only rotation. See {@link #PositionTransformer
	 * (IBlockStorage, int, int, int, double, boolean)} */
	public PositionTransformer(IBlockStorage storage, double angle, boolean closeGaps) {
		this(storage, 0, 0, 0, angle, closeGaps);
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		transform(x + 0.5, y, z + 0.5);
		return storage.getBlock((int)vec3.x, (int)vec3.y, (int)vec3.z);
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		transform(x + 0.5, y, z + 0.5);
		storage.setBlock((int)vec3.x, (int)vec3.y, (int)vec3.z, block);
		if (closeGaps) {
			storage.setBlock(MathUtil.roundDown(vec3.x), (int)vec3.y, (int)vec3.z, block);
			storage.setBlock((int)vec3.x, (int)vec3.y, MathUtil.roundDown(vec3.z), block);
			storage.setBlock(MathUtil.roundDown(vec3.x), (int)vec3.y, MathUtil.roundDown(vec3.z), block);
		}
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		transform(x + 0.5, y, z + 0.5);
		storage.clearBlock((int)vec3.x, (int)vec3.y, (int)vec3.z);
		if (closeGaps) {
			storage.clearBlock(MathUtil.roundDown(vec3.x), (int)vec3.y, (int)vec3.z);
			storage.clearBlock((int)vec3.x, (int)vec3.y, MathUtil.roundDown(vec3.z));
			storage.clearBlock(MathUtil.roundDown(vec3.x), (int)vec3.y, MathUtil.roundDown(vec3.z));
		}
	}

	/** Performs rotation and translation. */
	private Vec3 transform(double x, double y, double z) {
		// Apply rotation:
		vec2.set(x, z);
		rot.multiply(vec2);
		// Apply translation:
		return vec3.set(vec2.x + tx, y + ty, vec2.y + tz);
	}
}
