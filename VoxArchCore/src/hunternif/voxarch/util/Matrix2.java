package hunternif.voxarch.util;

public class Matrix2 {
	//m<row><column>
	public double m00, m01, m10, m11;
	
	/**
	 * Creates a 2D rotation matrix.
	 * @param angle in degrees.
	 */
	public static Matrix2 rotationMatrix(double angle) {
		double sin = MathUtil.sinDeg(angle);
		double cos = MathUtil.cosDeg(angle);
		Matrix2 mat = new Matrix2();
		mat.m00 = cos;
		mat.m01 = -sin;
		mat.m10 = sin;
		mat.m11 = cos;
		return mat;
	}
	
	/** Product of this matrix and the specified vector. Returns the modified
	 * vector argument. */
	public Vec2 multiply(Vec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = nx;
		vec.y = ny;
		return vec;
	}
	
	/** See {@link #rotationMatrix(double)}. Truncates mantissa when casting to integer. */
	public IntVec2 multiplyTruncate(IntVec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = (int)nx;
		vec.y = (int)ny;
		return vec;
	}
	
	/** See {@link #rotationMatrix(double)}. Rounds when casting to integer. */
	public IntVec2 multiplyRound(IntVec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = (int)Math.round(nx);
		vec.y = (int)Math.round(ny);
		return vec;
	}
	
	/** See {@link #rotationMatrix(double)}. Returns ceilingAbs when casting to integer.
	 * @see MathUtil#ceilingAbs(double)*/
	public IntVec2 multiplyCeiling(IntVec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = MathUtil.ceilingAbs(nx);
		vec.y = MathUtil.ceilingAbs(ny);
		return vec;
	}
}
