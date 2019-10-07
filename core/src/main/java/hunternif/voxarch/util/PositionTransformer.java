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
	/** Angle of rotation. Need to remember it to rotate the blocks correctly. */
	private double angle = 0;
	private boolean closeGaps = false;
	
	/** Stack for transformations. */
	private final Deque<StackData> stack = new ArrayDeque<StackData>();
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
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		return storage.getBlock(MathUtil.roundDown(vec.x), (int)vec.y, MathUtil.roundDown(vec.z));
	}

	@Override
	public void setBlock(int x, int y, int z, BlockData block) {
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		block.rotate(angle);
		storage.setBlock(MathUtil.roundDown(vec.x), (int)vec.y, MathUtil.roundDown(vec.z), block);
		if (closeGaps) {
			storage.setBlock((int)vec.x, (int)vec.y, (int)vec.z, block);
			storage.setBlock(MathUtil.roundDown(vec.x), (int)vec.y, (int)vec.z, block);
			storage.setBlock((int)vec.x, (int)vec.y, MathUtil.roundDown(vec.z), block);
		}
	}

	@Override
	public void clearBlock(int x, int y, int z) {
		vec.set(x, y, z, 1);
		matrix.multiplyLocal(vec);
		storage.clearBlock(MathUtil.roundDown(vec.x), (int)vec.y, MathUtil.roundDown(vec.z));
		// We want to leave no odd blocks in the volume when clearing:
		//if (closeGaps) {
			storage.clearBlock((int)vec.x, (int)vec.y, (int)vec.z);
			storage.clearBlock(MathUtil.roundDown(vec.x), (int)vec.y, (int)vec.z);
			storage.clearBlock((int)vec.x, (int)vec.y, MathUtil.roundDown(vec.z));
		//}
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

	/** Apply transformation of rotation around the Y axis. */
	public PositionTransformer rotateY(double angle) {
		this.angle += angle;
		matrix = matrix.multiplyLocal(Matrix4.rotationY(angle));
		return this;
	}

	/** If true, will apply every operation to the whole area that the rotated
	 * block covers thereby eliminating gaps caused by aliasing when rotating
	 * at a non-right angle. */
	public PositionTransformer setCloseGaps(boolean closeGaps) {
		this.closeGaps = closeGaps;
		return this;
	}
	
	/** Push current transformation info into stack. Similar to OpenGL
	 * glPushMatrix(). */
	public void pushTransformation() {
		StackData data = new StackData();
		data.angle = angle;
		data.matrix = matrix.clone();
		stack.push(data);
	}
	
	/** Pop last transformation into from the stack. Similar to OpenGL
	 * glPopMatrix(). */
	public void popTransformation() {
		StackData data = stack.poll();
		angle = data.angle;
		matrix = data.matrix;
	}
}