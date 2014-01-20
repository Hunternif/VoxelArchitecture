package hunternif.voxarch.util;

/**
 * 3D vector of doubles.
 * @author Hunternif
 */
public class Vec3 {
	public double x;
	public double y;
	public double z;
	
	public Vec3(Vec3 vec) {
		this(vec.x, vec.y, vec.z);
	}
	
	public Vec3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3 set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec3 add(Vec3 vec) {
		return add(vec.x, vec.y, vec.z);
	}
	/** Modifies and returns itself. */
	public Vec3 add(double dx, double dy, double dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
		return this;
	}
	
	/** Modifies and returns itself. */
	public Vec3 subtract(Vec3 vec) {
		return subtract(vec.x, vec.y, vec.z);
	}
	/** Modifies and returns itself. */
	public Vec3 subtract(double dx, double dy, double dz) {
		this.x -= dx;
		this.y -= dy;
		this.z -= dz;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	@Override
	public Vec3 clone() {
		return new Vec3(x, y, z);
	}
	
	public double distanceTo(Vec3 vec2) {
		return Math.sqrt((x-vec2.x)*(x-vec2.x) + (y-vec2.y)*(y-vec2.y) + (z-vec2.z)*(z-vec2.z));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vec3))
			return false;
		Vec3 vec = (Vec3) obj;
		return vec.x == x && vec.y == y && vec.z == z;
	}
	
	public boolean equalsIntVec3(Vec3 vec) {
		return vec.x == x && vec.y == y;
	}
}
