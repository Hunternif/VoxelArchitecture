package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.util.IntVec2;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.util.Matrix2;
import hunternif.voxarch.util.Vec2;

import org.junit.Test;

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
		assertEquals(MathUtil.sinDeg(45), MathUtil.cosDeg(45), 0.000000001);
	}
	
	@Test
	public void testRotateVector() {
		Matrix2 mat = Matrix2.rotationMatrix(90);
		Vec2 vec = new Vec2(1.5, 0);
		assertEquals(new Vec2(0, 1.5), mat.multiply(vec));
		
		vec.set(1, 0);
		Matrix2.rotationMatrix(-135).multiply(vec);
		assertEquals(-1d/Math.sqrt(2), vec.x, 0.00000001);
		assertEquals(-1d/Math.sqrt(2), vec.y, 0.00000001);
		
		mat = Matrix2.rotationMatrix(-45);
		IntVec2 intVec = new IntVec2(1, 0);
		assertEquals(new IntVec2(0, 0), mat.multiplyTruncate(intVec));
		intVec.set(1, 0);
		assertEquals(new IntVec2(1, -1), mat.multiplyRound(intVec));
		intVec.set(1, 0);
		assertEquals(new IntVec2(1, -1), mat.multiplyCeiling(intVec));
	}
	
	@Test
	public void testRotateMatrix() {
		Matrix2 rot = Matrix2.rotationMatrix(90);
		Matrix2 a = Matrix2.identity();
		assertEquals(new Matrix2(0, -1,
								 1,  0), rot.multiply(a));
		assertEquals(new Matrix2(-1,  0,
				 				  0, -1), rot.multiply(a));
		assertEquals(new Matrix2( 0, 1,
								 -1, 0), rot.multiply(a));
		
		rot = Matrix2.rotationMatrix(45);
		double s = MathUtil.sinDeg(45);
		assertEquals(new Matrix2(MathUtil.cosDeg(45), -MathUtil.sinDeg(45),
								 MathUtil.sinDeg(45),  MathUtil.cosDeg(45)),
					 rot.multiply(Matrix2.identity()));
	}
}