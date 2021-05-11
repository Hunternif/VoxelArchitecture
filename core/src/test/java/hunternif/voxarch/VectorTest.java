package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.util.VectorUtil;
import hunternif.voxarch.vector.Vec2;

import hunternif.voxarch.vector.Vec3;
import org.junit.Test;

/**
 * 
 * @author Hunternif
 *
 */
public class VectorTest {
	@Test
	public void testClosestPoint() {
		Vec2 a = new Vec2(0, 0);
		Vec2 b = new Vec2(1, 0);
		assertEquals(new Vec2(0, 0), VectorUtil.closestPointOnLineSegment(new Vec2(0, 0), a, b));
		assertEquals(new Vec2(0, 0), VectorUtil.closestPointOnLineSegment(new Vec2(-1, 0), a, b));
		assertEquals(new Vec2(1, 0), VectorUtil.closestPointOnLineSegment(new Vec2(2, 0), a, b));
		assertEquals(new Vec2(0.5, 0), VectorUtil.closestPointOnLineSegment(new Vec2(0.5, 0.5), a, b));
		assertEquals(new Vec2(0.5, 0), VectorUtil.closestPointOnLineSegment(new Vec2(0.5, -0.5), a, b));
		assertEquals(new Vec2(1, 0), VectorUtil.closestPointOnLineSegment(new Vec2(10, 20), a, b));
	}
	
	@Test
	public void testRayTrace() {
		Vec2 r1 = new Vec2(0, 0);
		Vec2 r2 = new Vec2(1, 0);
		assertEquals(new Vec2(2.5, 0), VectorUtil.rayTrace(r1, r2, new Vec2(2, 1), new Vec2(3, -1)));
		assertEquals(new Vec2(-2.5, 0), VectorUtil.rayTrace(r2, r1, new Vec2(-2, 1), new Vec2(-3, -1)));
		assertEquals(null, VectorUtil.rayTrace(r1, r2, new Vec2(0, 0), new Vec2(1, 0)));
		assertEquals(null, VectorUtil.rayTrace(r1, r2, new Vec2(1, 1), new Vec2(2, 1)));
		assertEquals(null, VectorUtil.rayTrace(r1, r2, new Vec2(10, 0), new Vec2(20, 0)));
		assertEquals(new Vec2(0, 0), VectorUtil.rayTrace(r1, r2, new Vec2(0, 0), new Vec2(1, 1)));
		assertNull(VectorUtil.rayTrace(r1, r2, new Vec2(-1, -1), new Vec2(-1, 1)));
	}

	@Test
	public void testIsInteger() {
		assertTrue(new Vec3(1, 2, 3).isInteger());
		assertFalse(new Vec3(1.1, 2, 3).isInteger());
		assertFalse(new Vec3(1, -2.1, 3).isInteger());
		assertFalse(new Vec3(1, 2, 3.1).isInteger());
	}
}
