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

	/** Multiplies all values in this matrix by value. */
	public Matrix4 multiplyLocal(double value) {
		this.m00 *= value;
		this.m01 *= value;
		this.m02 *= value;
		this.m03 *= value;
		this.m10 *= value;
		this.m11 *= value;
		this.m12 *= value;
		this.m13 *= value;
		this.m20 *= value;
		this.m21 *= value;
		this.m22 *= value;
		this.m23 *= value;
		this.m30 *= value;
		this.m31 *= value;
		this.m32 *= value;
		this.m33 *= value;
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

	private double adjugateAndDet() {
		double f = m00 * m11 - m01 * m10;
		double f1 = m00 * m12 - m02 * m10;
		double f2 = m00 * m13 - m03 * m10;
		double f3 = m01 * m12 - m02 * m11;
		double f4 = m01 * m13 - m03 * m11;
		double f5 = m02 * m13 - m03 * m12;
		double f6 = m20 * m31 - m21 * m30;
		double f7 = m20 * m32 - m22 * m30;
		double f8 = m20 * m33 - m23 * m30;
		double f9 = m21 * m32 - m22 * m31;
		double f10 = m21 * m33 - m23 * m31;
		double f11 = m22 * m33 - m23 * m32;
		double f12 = m11 * f11 - m12 * f10 + m13 * f9;
		double f13 = -m10 * f11 + m12 * f8 - m13 * f7;
		double f14 = m10 * f10 - m11 * f8 + m13 * f6;
		double f15 = -m10 * f9 + m11 * f7 - m12 * f6;
		double f16 = -m01 * f11 + m02 * f10 - m03 * f9;
		double f17 = m00 * f11 - m02 * f8 + m03 * f7;
		double f18 = -m00 * f10 + m01 * f8 - m03 * f6;
		double f19 = m00 * f9 - m01 * f7 + m02 * f6;
		double f20 = m31 * f5 - m32 * f4 + m33 * f3;
		double f21 = -m30 * f5 + m32 * f2 - m33 * f1;
		double f22 = m30 * f4 - m31 * f2 + m33 * f;
		double f23 = -m30 * f3 + m31 * f1 - m32 * f;
		double f24 = -m21 * f5 + m22 * f4 - m23 * f3;
		double f25 = m20 * f5 - m22 * f2 + m23 * f1;
		double f26 = -m20 * f4 + m21 * f2 - m23 * f;
		double f27 = m20 * f3 - m21 * f1 + m22 * f;
		this.m00 = f12;
		this.m10 = f13;
		this.m20 = f14;
		this.m30 = f15;
		this.m01 = f16;
		this.m11 = f17;
		this.m21 = f18;
		this.m31 = f19;
		this.m02 = f20;
		this.m12 = f21;
		this.m22 = f22;
		this.m32 = f23;
		this.m03 = f24;
		this.m13 = f25;
		this.m23 = f26;
		this.m33 = f27;
		return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
	}

	/** Converts this matrix to its inverse. Returns true if successful. */
	public boolean invertLocal() {
		double f = this.adjugateAndDet();
		if (Math.abs(f) > 1.0E-6F) {
			this.multiplyLocal(f);
			return true;
		} else {
			return false;
		}
	}

	/** Returns a new matrix that is the inverse of the current one. */
	public Matrix4 invert() {
		Matrix4 mat = this.clone();
		mat.invertLocal();
		return mat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Matrix4 matrix4 = (Matrix4) o;

		if (matrix4.m01 != m01) return false;
		if (matrix4.m02 != m02) return false;
		if (matrix4.m03 != m03) return false;
		if (matrix4.m10 != m10) return false;
		if (matrix4.m11 != m11) return false;
		if (matrix4.m12 != m12) return false;
		if (matrix4.m13 != m13) return false;
		if (matrix4.m20 != m20) return false;
		if (matrix4.m21 != m21) return false;
		if (matrix4.m22 != m22) return false;
		if (matrix4.m23 != m23) return false;
		if (matrix4.m30 != m30) return false;
		if (matrix4.m31 != m31) return false;
		if (matrix4.m32 != m32) return false;
		return matrix4.m33 == m33;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(m00);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m01);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m02);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m03);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m10);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m11);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m12);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m13);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m20);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m21);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m22);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m23);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m30);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m31);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m32);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m33);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
