package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.AlignedHorGateFactory;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import org.junit.Test;

public class HorGateTest {
	private IGateFactory gateFactory = new AlignedHorGateFactory();
	
	@Test
	public void testHorGateNoWalls() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(null, new Vec3(2, 1, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(1, 1.5, 0), gate.getOrigin());
		assertEquals(new Vec2(3, 0.5), gate.getSize());
		assertEquals(90, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	@Test
	public void testHorGateNoWallsRotated() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(1, gate.getOrigin().y, 0);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.000001);
	}
	
	@Test
	public void testHorGateWallsRotated() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		// Aligned with wall of room2:
		assertEquals(270, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void testHorGateWallsRotated2() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		room1.createFourWalls();
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		// Aligned with wall of room1:
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
}
