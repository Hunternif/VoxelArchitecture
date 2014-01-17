package hunternif.voxarch;

import static junit.framework.Assert.*;
import hunternif.voxarch.util.MathUtil;

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
	}
	
	@Test
	public void testClampAngle() {
		assertEquals(90f, MathUtil.clampAngle(90));
		assertEquals(0f, MathUtil.clampAngle(360));
		assertEquals(270f, MathUtil.clampAngle(-90));
		assertEquals(2f, MathUtil.clampAngle(360*11 + 2));
		assertEquals(358f, MathUtil.clampAngle(360*11 - 2));
		assertEquals(2f, MathUtil.clampAngle(-360*11 + 2));
	}
}
