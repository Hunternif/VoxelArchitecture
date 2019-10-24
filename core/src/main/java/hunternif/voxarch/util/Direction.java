package hunternif.voxarch.util;


/**
 * Used by blocks that can be rotated, such as stairs and wall-mounted
 * decorations.
 * @author Hunternif
 */
public enum Direction {
	EAST(0), NORTH(90), WEST(180), SOUTH(270);
	
	public static final Direction DEFAULT = EAST;
	
	public final double angle;
	private static final Direction[] rotations = {EAST, NORTH, WEST, SOUTH};
	
	Direction(double angle) {
		this.angle = angle;
	}
	
	/**
	 * Returns the orientation that is the closest to the specified angle
	 * [degrees].<p>
	 * On edge cases (i.e. 45 degrees) the orientation which comes first on
	 * counterclockwise rotation will be returned.
	 * </p>
	 */
	public static Direction closestTo(double angle) {
		angle = MathUtil.clampAngle(angle);
		int index = MathUtil.roundUp(angle / 90);
		if (index >= rotations.length) index = 0;
		return rotations[index];
	}

	public Direction getOpposite() {
		switch (this) {
			default:
			case EAST: return WEST;
			case NORTH: return SOUTH;
			case WEST: return EAST;
			case SOUTH: return NORTH;
		}
	}

	/**
	 * Returns the closest orientation which comes first in the direction of the
	 * rotation (positive angle is counterclockwise rotation).
	 */
	public Direction rotate(double angle) {
		boolean clockwise = angle < 0;
		angle += this.angle;
		angle = MathUtil.clampAngle(angle);
		
		int index = clockwise ? MathUtil.roundUp(angle / 90) : MathUtil.roundDown(angle / 90);
		if (index >= rotations.length) index = 0;
		return rotations[index];
	}
}
