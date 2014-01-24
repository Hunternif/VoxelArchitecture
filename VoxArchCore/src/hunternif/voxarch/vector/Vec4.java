package hunternif.voxarch.vector;

/**
 * 4D vector of doubles.
 * @author Hunternif
 */
public class Vec4 {
	public double x, y, z, s;
	
	public Vec4(Vec4 vec) {
		this(vec.x, vec.y, vec.z, vec.s);
	}
	
	public Vec4(double x, double y, double z, double s) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.s = s;
	}
	
	public Vec4 set(double x, double y, double z, double s) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.s = s;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec4 add(Vec4 vec) {
		return add(vec.x, vec.y, vec.z, vec.s);
	}
	/** Modifies and returns itself. */
	public Vec4 add(double dx, double dy, double dz, double ds) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
		this.s += ds;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec4 subtract(Vec4 vec) {
		return subtract(vec.x, vec.y, vec.z, vec.s);
	}
	/** Modifies and returns itself. */
	public Vec4 subtract(double dx, double dy, double dz, double ds) {
		this.x -= dx;
		this.y -= dy;
		this.z -= dz;
		this.s -= ds;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + s + ")";
	}
	
	@Override
	public Vec4 clone() {
		return new Vec4(x, y, z, s);
	}
	
	public double squareDistanceTo(Vec4 vec) {
		return Math.sqrt((x-vec.x)*(x-vec.x) + (y-vec.y)*(y-vec.y)
				+ (z-vec.z)*(z-vec.z) + (s-vec.s)*(s-vec.s));
	}
	public double distanceTo(Vec4 vec) {
		return Math.sqrt(squareDistanceTo(vec));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vec4))
			return false;
		Vec4 vec = (Vec4) obj;
		return vec.x == x && vec.y == y && vec.z == z && vec.s == s;
	}
}
