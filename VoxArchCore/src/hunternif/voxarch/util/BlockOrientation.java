package hunternif.voxarch.util;

/** Used by blocks that can be rotated, such as stairs and wall-attached
 * decorations. */
public enum BlockOrientation {
	NORTH(0), EAST(90), SOUTH(180), WEST(270);
	
	public final float angle;
	private BlockOrientation(float angle) {
		this.angle = angle;
	}
	
	/** Returns the orientation that is the closest to the specified angle. */
	public static BlockOrientation closestTo(float angle) {
		while (angle < 0) angle += 360;
		while (angle > 360) angle -= 360;
		float minDelta = Float.MAX_VALUE;
		BlockOrientation closest = NORTH;
		for (BlockOrientation orient : values()) {
			float delta = angle - orient.angle;
			if (delta < 0) delta += 360;
			delta = Math.min(delta, 360 - delta);
			if (delta < minDelta) {
				minDelta = delta;
				closest = orient;
			}
		}
		return closest;
	}
}
