package hunternif.voxarch.vector;

/**
 * 2D vector of doubles.
 * @author Hunternif
 */
public class Vec2 {
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
	
	/** Modifies and returns itself. */
	public Vec2 add(Vec2 vec) {
		return add(vec.x, vec.y);
	}
	/** Modifies and returns itself. */
	public Vec2 add(double dx, double dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec2 subtract(Vec2 vec) {
		return subtract(vec.x, vec.y);
	}
	/** Modifies and returns itself. */
	public Vec2 subtract(double dx, double dy) {
		this.x -= dx;
		this.y -= dy;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec2 multiply(double m) {
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
	public double distanceTo(Vec2 vec) {
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
}
