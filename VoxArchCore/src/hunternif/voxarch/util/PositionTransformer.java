package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.vector.Matrix4;
import hunternif.voxarch.vector.Vec4;

/**
 * A shell over storage, helper class for transforming block coordinates.
 * @author Hunternif
 */
public class PositionTransformer implements IBlockStorage {
	private final IBlockStorage storage;
	/** Angle of rotation. */
	private double angle = 0;
	private boolean closeGaps = false;
	
	/** Transformation matrix. */
	private Matrix4 matrix = Matrix4.identity();
	
	/** Saving memory by reusing the same vector. */
	private final Vec4 vec = new Vec4(0, 0, 0, 1);
	
	public PositionTransformer(IBlockStorage storage) {
		this.storage = storage;
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		vec.set(x + 0.5, y, z + 0.5, 1);
		matrix.multiply(vec);
		return storage.getBlock((int)vec.x, (int)vec.y, (int)vec.z);
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		vec.set(x + 0.5, y, z + 0.5, 1);
		matrix.multiply(vec);
		block.rotate((float)angle);
		storage.setBlock((int)vec.x, (int)vec.y, (int)vec.z, block);
		if (closeGaps) {
			storage.setBlock(MathUtil.roundDown(vec.x), (int)vec.y, (int)vec.z, block);
			storage.setBlock((int)vec.x, (int)vec.y, MathUtil.roundDown(vec.z), block);
			storage.setBlock(MathUtil.roundDown(vec.x), (int)vec.y, MathUtil.roundDown(vec.z), block);
		}
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		vec.set(x + 0.5, y, z + 0.5, 1);
		matrix.multiply(vec);
		storage.clearBlock((int)vec.x, (int)vec.y, (int)vec.z);
		if (closeGaps) {
			storage.clearBlock(MathUtil.roundDown(vec.x), (int)vec.y, (int)vec.z);
			storage.clearBlock((int)vec.x, (int)vec.y, MathUtil.roundDown(vec.z));
			storage.clearBlock(MathUtil.roundDown(vec.x), (int)vec.y, MathUtil.roundDown(vec.z));
		}
	}

	/** Apply transformation of translation. */
	public PositionTransformer translation(double x, double y, double z) {
		Matrix4.translation(x, y, z).multiply(matrix);
		return this;
	}

	/** Apply transformation of rotation around the Y axis. */
	public PositionTransformer rotationY(double angle) {
		this.angle += angle;
		Matrix4.rotationY(angle).multiply(matrix);
		return this;
	}

	/** If true, will apply every operation to the whole area that the rotated
	 * block covers thereby eliminating gaps caused by aliasing when rotating
	 * at a non-right angle. */
	public PositionTransformer setCloseGaps(boolean closeGaps) {
		this.closeGaps = closeGaps;
		return this;
	}
}
