package hunternif.voxarch.vector;

/**
 * 2D vector of doubles.
 * @author Hunternif
 */
public class Vec2 {
	public static final Vec2 ZERO = new Vec2(0, 0);
	public static final Vec2 UNIT_X = new Vec2(1, 0);
	public static final Vec2 UNIT_Y = new Vec2(0, 1);
	
	public double x;
	public double y;
	
	public Vec2(Vec2 vec) {
		this(vec.x, vec.y);
	}
	
	public Vec2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/** Creates a new instance. */
	public Vec2 add(Vec2 vec) {
		return new Vec2(this.x + vec.x, this.y + vec.y);
	}
	/** Modifies and returns itself. */
	public Vec2 addLocal(Vec2 vec) {
		return addLocal(vec.x, vec.y);
	}
	/** Modifies and returns itself. */
	public Vec2 addLocal(double dx, double dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}
	
	/** Creates a new instance. */
	public Vec2 subtract(Vec2 vec) {
		return new Vec2(this.x - vec.x, this.y - vec.y);
	}
	/** Modifies and returns itself. */
	public Vec2 subtractLocal(Vec2 vec) {
		return subtractLocal(vec.x, vec.y);
	}
	/** Modifies and returns itself. */
	public Vec2 subtractLocal(double dx, double dy) {
		this.x -= dx;
		this.y -= dy;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec2 multiplyLocal(double m) {
		this.x *= m;
		this.y *= m;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public Vec2 clone() {
		return new Vec2(x, y);
	}
	
	public double squareDistanceTo(Vec2 vec) {
		return (x-vec.x)*(x-vec.x) + (y-vec.y)*(y-vec.y);
	}
	public double squareDistanceTo(IntVec2 vec) {
		return (x-vec.x)*(x-vec.x) + (y-vec.y)*(y-vec.y);
	}
	public double distanceTo(Vec2 vec) {
		return Math.sqrt(squareDistanceTo(vec));
	}
	public double distanceTo(IntVec2 vec) {
		return Math.sqrt(squareDistanceTo(vec));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vec2))
			return false;
		Vec2 vec = (Vec2) obj;
		return vec.x == x && vec.y == y;
	}
	
	public double dotProduct(Vec2 vec) {
		return x*vec.x + y*vec.y;
	}
	
	public double length() {
		return distanceTo(ZERO);
	}
	
	/** Normalizes and returns itself. */
	public Vec2 normalizeLocal() {
		double length = length();
		if (length == 0) return this;
		x /= length;
		y /= length;
		return this;
	}
	
	public static Vec2 fromXZ(Vec3 vec) {
		return new Vec2 (vec.x, vec.z);
	}

	public IntVec2 toInt() {
		return new IntVec2(Math.round(x), Math.round(y));
	}
}
