package hunternif.voxarch.util;

/**
 * Used by blocks that can be rotated, such as stairs and wall-attached
 * decorations.
 */
public enum BlockOrientation {
	NONE(0), EAST(0), NORTH(90), WEST(180), SOUTH(270);
	
	public final float angle;
	private static final BlockOrientation[] rotations = {EAST, NORTH, WEST, SOUTH};
	
	private BlockOrientation(float angle) {
		this.angle = angle;
	}
	
	/**
	 * Returns the orientation that is the closest to the specified angle
	 * [degrees].<p>
	 * On edge cases (i.e. 45 degrees) the orientation which comes first on
	 * counterclockwise rotation will be returned.
	 * </p>
	 */
	public static BlockOrientation closestTo(float angle) {
		angle = MathUtil.clampAngle(angle);
		int index = MathUtil.roundUp(angle / 90);
		if (index >= rotations.length) index = 0;
		return rotations[index];
	}
	
	/**
	 * Returns the closest orientation which comes first in the direction of the
	 * rotation (positive angle is counterclockwise rotation).
	 */
	public BlockOrientation rotate(float angle) {
		if (this == NONE) return this;
		
		boolean clockwise = angle < 0;
		angle += this.angle;
		angle = MathUtil.clampAngle(angle);
		
		int index = clockwise ? MathUtil.roundUp(angle / 90) : MathUtil.roundDown(angle / 90);
		if (index >= rotations.length) index = 0;
		return rotations[index];
	}
}
