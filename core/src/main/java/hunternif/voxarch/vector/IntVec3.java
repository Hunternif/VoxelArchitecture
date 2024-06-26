package hunternif.voxarch.vector;

/**
 * 3D vector of doubles.
 * @author Hunternif
 */
public class IntVec3 {
	public int x;
	public int y;
	public int z;
	
	public IntVec3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public IntVec3(float x, float y, float z) {
		this.x = (int)x;
		this.y = (int)y;
		this.z = (int)z;
	}
	
	public IntVec3(double x, double y, double z) {
		this.x = (int)x;
		this.y = (int)y;
		this.z = (int)z;
	}
	
	public IntVec3 set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public IntVec3 set(IntVec3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}

	public IntVec3 add(IntVec3 p) {
		return new IntVec3(x + p.x, y + p.y, z + p.z);
	}

	public IntVec3 add(int dx, int dy, int dz) {
		return new IntVec3(x + dx, y + dy, z + dz);
	}

	/** Modifies and returns itself. */
	public IntVec3 addLocal(int dx, int dy, int dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
		return this;
	}

	/** Modifies and returns itself. */
	public IntVec3 addLocal(IntVec3 p) {
		this.x += p.x;
		this.y += p.y;
		this.z += p.z;
		return this;
	}

	public IntVec3 subtract(IntVec3 p) {
		return new IntVec3(x - p.x, y - p.y, z - p.z);
	}

	/** Modifies and returns itself. */
	public IntVec3 subtractLocal(double dx, double dy, double dz) {
		this.x -= dx;
		this.y -= dy;
		this.z -= dz;
		return this;
	}

	/** Modifies and returns itself. */
	public IntVec3 subtractLocal(int dx, int dy, int dz) {
		this.x -= dx;
		this.y -= dy;
		this.z -= dz;
		return this;
	}

	/** Returns a new vector. */
	public IntVec3 multiply(int m) {
		return new IntVec3(x*m, y*m, z*m);
	}

	/** Returns a new vector. */
	public IntVec3 multiply(double m) {
		return new IntVec3(x*m, y*m, z*m);
	}

	/** Modifies and returns itself. */
	public IntVec3 multiplyLocal(double m) {
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
	public IntVec3 clone() {
		return new IntVec3(x, y, z);
	}
	
	public double distanceTo(IntVec3 intVec3) {
		double x1 = x;
		double y1 = y;
		double z1 = z;
		double x2 = intVec3.x;
		double y2 = intVec3.y;
		double z2 = intVec3.z;
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntVec3))
			return false;
		IntVec3 vec = (IntVec3) obj;
		return vec.x == x && vec.y == y && vec.z == z;
	}
	
	@Override
	public int hashCode() {
		return x + (z << 16) + (y << 25);
	}
	
	public boolean equalsIntVec3(IntVec3 vec) {
		return vec.x == x && vec.y == y && vec.z == z;
	}

	public IntVec2 toXZ() {
		return new IntVec2(x, z);
	}

	public Vec3 toVec3() {
		return new Vec3(x, y, z);
	}

	public IntVec3 unaryMinus() {
		return multiply(-1);
	}

	public IntVec3 plus(IntVec3 vec) {
		return add(vec);
	}

	public IntVec3 minus(IntVec3 vec) {
		return subtract(vec);
	}

	public IntVec3 times(double t) {
		return new IntVec3(x * t, y * t, z * t);
	}

	public IntVec3 div(double t) {
		return new IntVec3(x / t, y / t, z / t);
	}

	public IntVec3 times(int t) {
		return new IntVec3(x * t, y * t, z * t);
	}

	public IntVec3 div(int t) {
		return new IntVec3(x / t, y / t, z / t);
	}
}
