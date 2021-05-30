package hunternif.voxarch.vector;

import org.jetbrains.annotations.Nullable;

/**
 * 3D vector of doubles.
 * @author Hunternif
 */
public class Vec3 {
	public static final Vec3 ZERO = new Vec3(0, 0, 0);
	public static final Vec3 UNIT_X = new Vec3(1, 0, 0);
	public static final Vec3 UNIT_Y = new Vec3(0, 1, 0);
	public static final Vec3 UNIT_Z = new Vec3(0, 0, 1);
	
	public double x;
	public double y;
	public double z;
	
	public Vec3(Vec3 vec) {
		this(vec.x, vec.y, vec.z);
	}

	public Vec3(IntVec3 vec) {
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
	public Vec3 addLocal(Vec3 vec) {
		return addLocal(vec.x, vec.y, vec.z);
	}
	/** Modifies and returns itself. */
	public Vec3 addLocal(double dx, double dy, double dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
		return this;
	}
	
	/** Returns a new vector. */
	public Vec3 add(Vec3 vec) {
		return add(vec.x, vec.y, vec.z);
	}
	/** Returns a new vector. */
	public Vec3 add(double dx, double dy, double dz) {
		return new Vec3(x + dx, y + dy, z + dz);
	}
	public Vec3 addX(double dx) {
		return new Vec3(x + dx, y, z);
	}
	public Vec3 addY(double dy) {
		return new Vec3(x, y + dy, z);
	}
	public Vec3 addZ(double dz) {
		return new Vec3(x, y, z + dz);
	}
	public Vec3 addX(int dx) {
		return new Vec3(x + dx, y, z);
	}
	public Vec3 addY(int dy) {
		return new Vec3(x, y + dy, z);
	}
	public Vec3 addZ(int dz) {
		return new Vec3(x, y, z + dz);
	}
	
	/** Returns a new vector. */
	public Vec3 subtract(Vec3 vec) {
		return new Vec3(x - vec.x, y - vec.y, z - vec.z);
	}
	/** Modifies and returns itself. */
	public Vec3 subtractLocal(Vec3 vec) {
		return subtractLocal(vec.x, vec.y, vec.z);
	}
	/** Returns a new vector. */
	public Vec3 subtract(double dx, double dy, double dz) {
		return new Vec3(x - dx, y - dy, z - dz);
	}
	/** Modifies and returns itself. */
	public Vec3 subtractLocal(double dx, double dy, double dz) {
		this.x -= dx;
		this.y -= dy;
		this.z -= dz;
		return this;
	}
	
	/** Returns a new vector. */
	public Vec3 multiply(double m) {
		return new Vec3(x*m, y*m, z*m);
	}
	/** Modifies and returns itself. */
	public Vec3 multiplyLocal(double m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
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
	
	public double squareDistanceTo(Vec3 vec) {
		return (x-vec.x)*(x-vec.x) + (y-vec.y)*(y-vec.y) + (z-vec.z)*(z-vec.z);
	}
	public double distanceTo(Vec3 vec) {
		return Math.sqrt(squareDistanceTo(vec));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vec3))
			return false;
		Vec3 vec = (Vec3) obj;
		return vec.x == x && vec.y == y && vec.z == z;
	}
	
	public double dotProduct(Vec3 vec) {
		return x*vec.x + y*vec.y + z*vec.z;
	}
	
	/** Returns a new vector that is the cross product [this x vec] */
	public Vec3 crossProduct(Vec3 vec) {
		return new Vec3(y*vec.z - z*vec.y, z*vec.x - x*vec.z, x*vec.y - y*vec.x);
	}
	
	public double length() {
		return distanceTo(ZERO);
	}
	
	/** Normalizes and returns itself. */
	public Vec3 normalizeLocal() {
		double length = length();
		if (length == 0) return this;
		x /= length;
		y /= length;
		z /= length;
		return this;
	}
	
	public static Vec3 from(Vec4 vec) {
		return new Vec3(vec.x, vec.y, vec.z);
	}
	
	public static Vec3 fromXZ(Vec2 vec) {
		return new Vec3(vec.x, 0, vec.y);
	}
	public Vec2 toXZ() {
		return new Vec2(x, z);
	}
	public IntVec3 toIntVec3() {
		return new IntVec3(x, y, z);
	}

	public boolean isInteger() {
		return x - Math.round(x) == 0 && y - Math.round(y) == 0 && z - Math.round(z) == 0;
	}

    public Vec3 unaryMinus() {
        return multiply(-1);
    }
}
