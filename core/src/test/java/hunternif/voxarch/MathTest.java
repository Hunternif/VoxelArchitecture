package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.IntVec2;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Matrix4;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec4;

import org.junit.Test;

/**
 * @author Hunternif
 */
public class MathTest {
	@Test
	public void testRound() {
		assertEquals(1, MathUtil.roundUp(1.4f));
		assertEquals(2, MathUtil.roundUp(1.5f));
		assertEquals(2, MathUtil.roundUp(1.6f));
		assertEquals(1, MathUtil.roundDown(1.4f));
		assertEquals(1, MathUtil.roundDown(1.5f));
		assertEquals(2, MathUtil.roundDown(1.6f));
		
		assertEquals(0, MathUtil.roundUp(0.1f));
		assertEquals(0, MathUtil.roundUp(-0.1f));
		assertEquals(0, MathUtil.roundDown(0.1f));
		assertEquals(0, MathUtil.roundDown(-0.1f));
		
		assertEquals(1, MathUtil.roundUp(0.5f));
		assertEquals(0, MathUtil.roundUp(-0.5f));
		assertEquals(0, MathUtil.roundDown(0.5f));
		assertEquals(-1, MathUtil.roundDown(-0.5f));
		
		assertEquals(0, MathUtil.roundUp(0.499f));
		assertEquals(1, MathUtil.roundDown(0.501f));
	}
	
	@Test
	public void testCeiling() {
		assertEquals(1, MathUtil.ceiling(0.501f));
		assertEquals(0, MathUtil.ceiling(-0.501f));
		assertEquals(1, MathUtil.ceilingAbs(0.501));
		assertEquals(-1, MathUtil.ceilingAbs(-0.501));
	}
	
	@Test
	public void testClampAngle() {
		assertEquals(90f, MathUtil.clampAngle(90), 0);
		assertEquals(0f, MathUtil.clampAngle(360), 0);
		assertEquals(270f, MathUtil.clampAngle(-90), 0);
		assertEquals(2f, MathUtil.clampAngle(360*11 + 2), 0);
		assertEquals(358f, MathUtil.clampAngle(360*11 - 2), 0);
		assertEquals(2f, MathUtil.clampAngle(-360*11 + 2), 0);
		assertEquals(1.3455676, MathUtil.clampAngle(361.3455676), 0.0000001);
	}
	
	@Test
	public void testSin() {
		assertEquals(0d, MathUtil.sinDeg(0), 0);
		assertEquals(0d, MathUtil.sinDeg(360), 0);
		assertEquals(1d, MathUtil.sinDeg(90), 0);
		assertEquals(1d, MathUtil.sinDeg(450), 0);
		assertEquals(-1d, MathUtil.sinDeg(-90), 0);
		assertEquals(-1d, MathUtil.sinDeg(270), 0);
		assertEquals(1d, MathUtil.cosDeg(0), 0);
		assertEquals(1d, MathUtil.cosDeg(-360), 0);
		assertEquals(0d, MathUtil.cosDeg(90), 0);
		assertEquals(0d, MathUtil.cosDeg(-450), 0);
		assertEquals(0d, MathUtil.cosDeg(-90), 0);
		assertEquals(0d, MathUtil.cosDeg(270), 0);
		assertEquals(-1d, MathUtil.cosDeg(180), 0);
		assertEquals(-1d, MathUtil.cosDeg(-180), 0);
		assertEquals(MathUtil.sinDeg(22.5), -MathUtil.cosDeg(112.5), 0.0001);
		assertEquals(MathUtil.sinDeg(22.5), MathUtil.cosDeg(67.5), 0.0001);
		assertEquals(MathUtil.sinDeg(45), MathUtil.cosDeg(45), 0.0001);
		assertEquals(0.5, MathUtil.sinDeg(30), 0.0001);
		assertEquals(0.5, MathUtil.sinDeg(150), 0.0001);
		assertEquals(-0.5, MathUtil.sinDeg(210), 0.0001);
		assertEquals(-0.5, MathUtil.sinDeg(330), 0.0001);
		assertEquals(0.5, MathUtil.cosDeg(60), 0.0001);
		assertEquals(-0.5, MathUtil.cosDeg(120), 0.0001);
		assertEquals(-0.5, MathUtil.cosDeg(240), 0.0001);
		assertEquals(0.5, MathUtil.cosDeg(300), 0.0001);
	}
	
	@Test
	public void testRotateVector2() {
		Matrix2 mat = Matrix2.rotationMatrix(90);
		Vec2 vec = new Vec2(1.5, 0);
		assertEquals(new Vec2(0, 1.5), mat.multiplyLocal(vec));
		
		vec.set(1, 0);
		Matrix2.rotationMatrix(-135).multiplyLocal(vec);
		assertEquals(-1d/Math.sqrt(2), vec.x, 0.00000001);
		assertEquals(-1d/Math.sqrt(2), vec.y, 0.00000001);
		
		mat = Matrix2.rotationMatrix(-45);
		IntVec2 intVec = new IntVec2(1, 0);
		assertEquals(new IntVec2(0, 0), mat.multiplyLocalTruncate(intVec));
		intVec.set(1, 0);
		assertEquals(new IntVec2(1, -1), mat.multiplyLocalRound(intVec));
		intVec.set(1, 0);
		assertEquals(new IntVec2(1, -1), mat.multiplyLocalCeiling(intVec));
	}
	
	@Test
	public void testMultiplyAndRotateMatrix2() {
		Matrix2 rot = Matrix2.rotationMatrix(90);
		Matrix2 a = Matrix2.identity();
		assertEquals(new Matrix2(0, -1,
								 1,  0), rot.multiplyLocal(a));
		assertEquals(new Matrix2(-1,  0,
				 				  0, -1), rot.multiplyLocal(a));
		assertEquals(new Matrix2( 0, 1,
								 -1, 0), rot.multiplyLocal(a));
		
		rot = Matrix2.rotationMatrix(45);
		assertEquals(new Matrix2(MathUtil.cosDeg(45), -MathUtil.sinDeg(45),
								 MathUtil.sinDeg(45),  MathUtil.cosDeg(45)),
					 rot.multiplyLocal(Matrix2.identity()));
	}
	
	@Test
	public void testRotateVector4AroundY() {
		Matrix4 mat = Matrix4.rotationY(90);
		Vec4 vec = new Vec4(1.5, 0, 0.5, 0);
		assertEquals(new Vec4(0.5, 0, -1.5, 0), mat.multiplyLocal(vec));
	}

	@Test
	public void testRotateVector4AroundX() {
		Matrix4 mat = Matrix4.rotationX(90);
		Vec4 vec = new Vec4(0, 1.5, 0.5, 0);
		assertEquals(new Vec4(0, -0.5, 1.5, 0), mat.multiplyLocal(vec));
	}

	@Test
	public void testRotateVector4AroundZ() {
		Matrix4 mat = Matrix4.rotationZ(90);
		Vec4 vec = new Vec4(1.5, 0.5, 0, 0);
		assertEquals(new Vec4(-0.5, 1.5, 0, 0), mat.multiplyLocal(vec));
	}
}
