package hunternif.voxarch.vector;

import hunternif.voxarch.util.MathUtil;

/**
 * 2x2 matrix of doubles.
 * @author Hunternif
 */
public class Matrix2 {
	/** m[row, column] */
	public double m00, m01, m10, m11;
	
	public Matrix2() {
		this(0, 0, 0, 0);
	}
	
	public Matrix2(double m00, double m01, double m10, double m11) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
	}
	
	/**
	 * Creates a 2D rotation matrix.
	 * @param angle in degrees.
	 */
	public static Matrix2 rotationMatrix(double angle) {
		Matrix2 mat = new Matrix2();
		mat.m00 = MathUtil.cosDeg(angle);
		mat.m01 = -MathUtil.sinDeg(angle);
		mat.m10 = -mat.m01;
		mat.m11 = mat.m00;
		return mat;
	}
	
	/**
	 * Creates a 2D identity matrix.
	 */
	public static Matrix2 identity() {
		Matrix2 mat = new Matrix2();
		mat.m00 = 1;
		mat.m01 = 0;
		mat.m10 = 0;
		mat.m11 = 1;
		return mat;
	}

	/** Create a new matrix with the specified vectors defining its columns. */
	public static Matrix2 composeFrom(Vec2 vec1, Vec2 vec2) {
		Matrix2 mat = new Matrix2();
		mat.m00 = vec1.x;
		mat.m10 = vec1.y;
		mat.m01 = vec2.x;
		mat.m11 = vec2.y;
		return mat;
	}
	
	/**
	 * Modifies this specified matrix to be the product (this * mat).
	 */
	public Matrix2 multiplyLocal(Matrix2 mat) {
		double n00 = m00*mat.m00 + m01*mat.m10;
		double n01 = m00*mat.m01 + m01*mat.m11;
		double n10 = m10*mat.m00 + m11*mat.m10;
		double n11 = m10*mat.m01 + m11*mat.m11;
		mat.m00 = n00;
		mat.m01 = n01;
		mat.m10 = n10;
		mat.m11 = n11;
		return mat;
	}
	
	/** Returns a new matrix as a result of transposition of this matrix. */
	public Matrix2 transpose() {
		Matrix2 mat = new Matrix2();
		mat.m00 = m00;
		mat.m01 = m10;
		mat.m10 = m01;
		mat.m11 = m11;
		return mat;
	}
	
	/**
	 * Modifies the specified vector to be the product of this matrix and itself.
	 */
	public Vec2 multiplyLocal(Vec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = nx;
		vec.y = ny;
		return vec;
	}
	
	/** See {@link #rotationMatrix(double)}. Truncates mantissa when casting to integer. */
	public IntVec2 multiplyLocalTruncate(IntVec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = (int)nx;
		vec.y = (int)ny;
		return vec;
	}
	
	/** See {@link #rotationMatrix(double)}. Rounds when casting to integer. */
	public IntVec2 multiplyLocalRound(IntVec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = (int)Math.round(nx);
		vec.y = (int)Math.round(ny);
		return vec;
	}
	
	/** See {@link #rotationMatrix(double)}. Returns ceilingAbs when casting to integer.
	 * @see MathUtil#ceilingAbs(double)*/
	public IntVec2 multiplyLocalCeiling(IntVec2 vec) {
		double nx = vec.x*m00 + vec.y*m01;
		double ny = vec.x*m10 + vec.y*m11;
		vec.x = MathUtil.ceilingAbs(nx);
		vec.y = MathUtil.ceilingAbs(ny);
		return vec;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Matrix2)) return false;
		Matrix2 mat = (Matrix2) obj;
		return m00 == mat.m00 && m01 == mat.m01 && m10 == mat.m10 && m11 == mat.m11;
	}
	
	@Override
	public String toString() {
		return m00 + " " + m01 + "\n" + m10 + " " + m11;
	}
	
	public double determinant() {
		return m00*m11 - m01*m10;
	}
}
