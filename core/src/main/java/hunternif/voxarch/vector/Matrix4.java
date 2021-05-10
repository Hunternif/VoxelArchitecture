package hunternif.voxarch.vector;

import hunternif.voxarch.util.MathUtil;

/**
 * 4x4 matrix of doubles.
 * @author Hunternif
 */
public class Matrix4 {
	/** m[row, column] */
	public double m00, m01, m02, m03,
				  m10, m11, m12, m13,
				  m20, m21, m22, m23,
				  m30, m31, m32, m33;
	
	/** Creates an identity matrix. */
	public static Matrix4 identity() {
		Matrix4 mat = new Matrix4();
		mat.m00 = 1;
		mat.m11 = 1;
		mat.m22 = 1;
		mat.m33 = 1;
		return mat;
	}
	
	/** Creates a transformation matrix of translation. */
	public static Matrix4 translation(double x, double y, double z) {
		Matrix4 mat = identity();
		mat.m03 = x;
		mat.m13 = y;
		mat.m23 = z;
		return mat;
	}
	/** Creates a transformation matrix of translation. */
	public static Matrix4 translationAdd(Vec3 vec) {
		return translation(vec.x, vec.y, vec.z);
	}
	/** Creates a transformation matrix of translation. */
	public static Matrix4 translationSubtract(Vec3 vec) {
		return translation(-vec.x, -vec.y, -vec.z);
	}
	
	/**
	 * Creates a transformation matrix of counterclockwise rotation around the Y axis.
	 * @param angle angle in degrees.
	 */
	public static Matrix4 rotationY(double angle) {
		Matrix4 mat = identity();
		mat.m00 = MathUtil.cosDeg(angle);
		mat.m02 = MathUtil.sinDeg(angle);
		mat.m20 = -mat.m02;
		mat.m22 = mat.m00;
		return mat;
	}

	/**
	 * Creates a transformation matrix of counterclockwise rotation around the X axis.
	 * @param angle angle in degrees.
	 */
	public static Matrix4 rotationX(double angle) {
		Matrix4 mat = identity();
		mat.m11 = MathUtil.cosDeg(angle);
		mat.m21 = MathUtil.sinDeg(angle);
		mat.m22 = mat.m11;
		mat.m12 = -mat.m21;
		return mat;
	}

	/**
	 * Creates a transformation matrix of counterclockwise rotation around the Z axis.
	 * @param angle angle in degrees.
	 */
	public static Matrix4 rotationZ(double angle) {
		Matrix4 mat = identity();
		mat.m00 = MathUtil.cosDeg(angle);
		mat.m10 = MathUtil.sinDeg(angle);
		mat.m11 = mat.m00;
		mat.m01 = -mat.m10;
		return mat;
	}
	
	/**
	 * Modifies <em>this matrix</em> to be the product (this * mat).
	 * @return this matrix.
	 */
	public Matrix4 multiplyLocal(Matrix4 mat) {
		double n00 = m00*mat.m00 + m01*mat.m10 + m02*mat.m20 + m03*mat.m30;
		double n01 = m00*mat.m01 + m01*mat.m11 + m02*mat.m21 + m03*mat.m31;
		double n02 = m00*mat.m02 + m01*mat.m12 + m02*mat.m22 + m03*mat.m32;
		double n03 = m00*mat.m03 + m01*mat.m13 + m02*mat.m23 + m03*mat.m33;
		double n10 = m10*mat.m00 + m11*mat.m10 + m12*mat.m20 + m13*mat.m30;
		double n11 = m10*mat.m01 + m11*mat.m11 + m12*mat.m21 + m13*mat.m31;
		double n12 = m10*mat.m02 + m11*mat.m12 + m12*mat.m22 + m13*mat.m32;
		double n13 = m10*mat.m03 + m11*mat.m13 + m12*mat.m23 + m13*mat.m33;
		double n20 = m20*mat.m00 + m21*mat.m10 + m22*mat.m20 + m23*mat.m30;
		double n21 = m20*mat.m01 + m21*mat.m11 + m22*mat.m21 + m23*mat.m31;
		double n22 = m20*mat.m02 + m21*mat.m12 + m22*mat.m22 + m23*mat.m32;
		double n23 = m20*mat.m03 + m21*mat.m13 + m22*mat.m23 + m23*mat.m33;
		double n30 = m30*mat.m00 + m31*mat.m10 + m32*mat.m20 + m33*mat.m30;
		double n31 = m30*mat.m01 + m31*mat.m11 + m32*mat.m21 + m33*mat.m31;
		double n32 = m30*mat.m02 + m31*mat.m12 + m32*mat.m22 + m33*mat.m32;
		double n33 = m30*mat.m03 + m31*mat.m13 + m32*mat.m23 + m33*mat.m33;
		this.m00 = n00;
		this.m01 = n01;
		this.m02 = n02;
		this.m03 = n03;
		this.m10 = n10;
		this.m11 = n11;
		this.m12 = n12;
		this.m13 = n13;
		this.m20 = n20;
		this.m21 = n21;
		this.m22 = n22;
		this.m23 = n23;
		this.m30 = n30;
		this.m31 = n31;
		this.m32 = n32;
		this.m33 = n33;
		return this;
	}
	
	/**
	 * Modifies <em>the specified matrix</em> to be the product (this * mat).
	 * @return the specified matrix.
	 */
	public Matrix4 multiply(Matrix4 mat) {
		double n00 = m00*mat.m00 + m01*mat.m10 + m02*mat.m20 + m03*mat.m30;
		double n01 = m00*mat.m01 + m01*mat.m11 + m02*mat.m21 + m03*mat.m31;
		double n02 = m00*mat.m02 + m01*mat.m12 + m02*mat.m22 + m03*mat.m32;
		double n03 = m00*mat.m03 + m01*mat.m13 + m02*mat.m23 + m03*mat.m33;
		double n10 = m10*mat.m00 + m11*mat.m10 + m12*mat.m20 + m13*mat.m30;
		double n11 = m10*mat.m01 + m11*mat.m11 + m12*mat.m21 + m13*mat.m31;
		double n12 = m10*mat.m02 + m11*mat.m12 + m12*mat.m22 + m13*mat.m32;
		double n13 = m10*mat.m03 + m11*mat.m13 + m12*mat.m23 + m13*mat.m33;
		double n20 = m20*mat.m00 + m21*mat.m10 + m22*mat.m20 + m23*mat.m30;
		double n21 = m20*mat.m01 + m21*mat.m11 + m22*mat.m21 + m23*mat.m31;
		double n22 = m20*mat.m02 + m21*mat.m12 + m22*mat.m22 + m23*mat.m32;
		double n23 = m20*mat.m03 + m21*mat.m13 + m22*mat.m23 + m23*mat.m33;
		double n30 = m30*mat.m00 + m31*mat.m10 + m32*mat.m20 + m33*mat.m30;
		double n31 = m30*mat.m01 + m31*mat.m11 + m32*mat.m21 + m33*mat.m31;
		double n32 = m30*mat.m02 + m31*mat.m12 + m32*mat.m22 + m33*mat.m32;
		double n33 = m30*mat.m03 + m31*mat.m13 + m32*mat.m23 + m33*mat.m33;
		mat.m00 = n00;
		mat.m01 = n01;
		mat.m02 = n02;
		mat.m03 = n03;
		mat.m10 = n10;
		mat.m11 = n11;
		mat.m12 = n12;
		mat.m13 = n13;
		mat.m20 = n20;
		mat.m21 = n21;
		mat.m22 = n22;
		mat.m23 = n23;
		mat.m30 = n30;
		mat.m31 = n31;
		mat.m32 = n32;
		mat.m33 = n33;
		return mat;
	}
	
	/**
	 * Modifies the specified vector to be the product of this matrix and itself.
	 */
	public Vec4 multiplyLocal(Vec4 vec) {
		double nx = vec.x*m00 + vec.y*m01 + vec.z*m02 + vec.s*m03;
		double ny = vec.x*m10 + vec.y*m11 + vec.z*m12 + vec.s*m13;
		double nz = vec.x*m20 + vec.y*m21 + vec.z*m22 + vec.s*m23;
		double ns = vec.x*m30 + vec.y*m31 + vec.z*m32 + vec.s*m33;
		vec.x = nx;
		vec.y = ny;
		vec.z = nz;
		vec.s = ns;
		return vec;
	}
	
	@Override
	public Matrix4 clone() {
		Matrix4 mat = new Matrix4();
		mat.m00 = m00;
		mat.m01 = m01;
		mat.m02 = m02;
		mat.m03 = m03;
		mat.m10 = m10;
		mat.m11 = m11;
		mat.m12 = m12;
		mat.m13 = m13;
		mat.m20 = m20;
		mat.m21 = m21;
		mat.m22 = m22;
		mat.m23 = m23;
		mat.m30 = m30;
		mat.m31 = m31;
		mat.m32 = m32;
		mat.m33 = m33;
		return mat;
	}
	
	@Override
	public String toString() {
		return m00 + " " + m01 + " " + m02 + " " + m03 + "\n" +
				m10 + " " + m11 + " " + m12 + " " + m13 + "\n" +
				m20 + " " + m21 + " " + m22 + " " + m23 + "\n" +
				m30 + " " + m31 + " " + m32 + " " + m33 + "\n";
	}
}
