package hunternif.voxarch.util;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.vector.Matrix4;
import hunternif.voxarch.vector.Vec3;
import hunternif.voxarch.vector.Vec4;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A helper class for transforming block coordinates. When calling
 * {@link #translate} and {@link #rotateY} successively think about translating
 * and rotating the reference frame, in the same sequence.
 * @author Hunternif
 */
public class PositionTransformer implements IBlockStorage {
	private final IBlockStorage storage;
	/** Angle of counterclockwise rotation. Need to remember it to rotate the blocks correctly. */
	private double angleY = 0;
	
	/** Stack for transformations. */
	private final Deque<StackData> stack = new ArrayDeque<>();
	private static class StackData {
		double angle;
		Matrix4 matrix;
	}
	
	/** Transformation matrix. */
	private Matrix4 matrix = Matrix4.identity();
	
	/** Saving memory by reusing the same vector. */
	private final Vec4 vec = new Vec4(0, 0, 0, 1);
	
	public PositionTransformer(IBlockStorage storage) {
		this.storage = storage;
	}

	@Override
	public BlockData getBlock(int x, int y, int z) {
		return getBlock((double)x, (double)y, (double)z);
	}

	public BlockData getBlock(double x, double y, double z) {
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		return storage.getBlock(MathUtil.roundDown(vec.x), MathUtil.roundDown(vec.y), MathUtil.roundDown(vec.z));
	}

	/**
	 * If this is the only transformer over the destination voxel world,
	 * then the result is the actual angle we're at relative to that world.
	 */
	public Double getAngleY() {
		return angleY;
	}

	/**
	 * Transforms the given coordinates and returns a new vector.
	 */
	public Vec3 transform(double x, double y, double z) {
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		return new Vec3(vec.x, vec.y, vec.z);
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		setBlock((double)x, (double)y, (double)z, block);
	}

	public void setBlock(double x, double y, double z, BlockData block) {
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		Direction cachedOrientation = block.getOrientation();
		block.rotate(angleY);
		storage.setBlock(MathUtil.roundDown(vec.x), MathUtil.roundDown(vec.y), MathUtil.roundDown(vec.z), block);
		block.setOrientation(cachedOrientation);
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		clearBlock((double)x, (double)y, (double)z);
	}

	public void clearBlock(double x, double y, double z) {
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		storage.clearBlock(MathUtil.roundDown(vec.x), (int)vec.y, MathUtil.roundDown(vec.z));
	}

	/** Apply transformation of translation. */
	public PositionTransformer translate(double x, double y, double z) {
		matrix = matrix.multiplyLocal(Matrix4.translation(x, y, z));
		return this;
	}
	
	/** Apply transformation of translation. */
	public PositionTransformer translate(Vec3 vec) {
		return translate(vec.x, vec.y, vec.z);
	}

	/** Apply transformation of rotation counterclockwise around the Y axis. */
	public PositionTransformer rotateY(double angle) {
		this.angleY += angle;
		matrix = matrix.multiplyLocal(Matrix4.rotationY(angle));
		return this;
	}

	//TODO: store rotation as vector? Currently the XZ rotations will not affect blocks.
	public PositionTransformer rotateX(double angle) {
		matrix = matrix.multiplyLocal(Matrix4.rotationX(angle));
		return this;
	}

	public PositionTransformer rotateZ(double angle) {
		matrix = matrix.multiplyLocal(Matrix4.rotationZ(angle));
		return this;
	}

	/** If true, will apply every operation to the whole area that the rotated
	 * block covers thereby eliminating gaps caused by aliasing when rotating
	 * at a non-right angle.
	 *
	 * Deprecated: use steps of 0.5 blocks to fill gaps.
	 */
	@Deprecated
	public PositionTransformer setCloseGaps(boolean closeGaps) {
		return this;
	}
	
	/** Push current transformation info into stack. Similar to OpenGL
	 * glPushMatrix(). */
	public void pushTransformation() {
		StackData data = new StackData();
		data.angle = angleY;
		data.matrix = matrix.clone();
		stack.push(data);
	}
	
	/** Pop last transformation into from the stack. Similar to OpenGL
	 * glPopMatrix(). */
	public void popTransformation() {
		StackData data = stack.poll();
		if (data != null) {
			angleY = data.angle;
			matrix = data.matrix;
		}
	}
}
