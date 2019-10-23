package hunternif.voxarch.vector;

import hunternif.voxarch.util.Direction;

/**
 * 2D vector of integers.
 * @author Hunternif
 */
public class IntVec2 {
	public int x;
	public int y;
	
	public IntVec2(IntVec2 vec) {
		this(vec.x, vec.y);
	}
	
	public IntVec2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public IntVec2(float x, float y) {
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public IntVec2(double x, double y) {
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public IntVec2 set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/** Returns new instance. */
	public IntVec2 add(int dx, int dy) {
		return new IntVec2(x + dx, y + dy);
	}

	/** Modifies and returns itself. */
	public IntVec2 addLocal(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public IntVec2 clone() {
		return new IntVec2(x, y);
	}
	
	public double distanceTo(IntVec2 intVec2) {
		double x1 = x;
		double y1 = y;
		double x2 = intVec2.x;
		double y2 = intVec2.y;
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntVec2))
			return false;
		IntVec2 vec = (IntVec2) obj;
		return vec.x == x && vec.y == y;
	}
	
	@Override
	public int hashCode() {
		return x + (y << 16);
	}
	
	public IntVec2 next(Direction direction) {
		switch (direction) {
			default:
			case EAST: return new IntVec2(x + 1, y);
			case NORTH: return new IntVec2(x, y - 1);
			case WEST: return new IntVec2(x - 1, y);
			case SOUTH: return new IntVec2(x, y + 1);
		}
	}
}
