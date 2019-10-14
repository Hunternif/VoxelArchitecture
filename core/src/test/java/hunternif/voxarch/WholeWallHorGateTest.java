package hunternif.voxarch;

import org.junit.Test;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.plan.gate.WholeWallHorGateFactory;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;
import static org.junit.Assert.*;

public class WholeWallHorGateTest {
	private IGateFactory gateFactory = new WholeWallHorGateFactory();
	
	@Test
	public void test1() {
		Room base = new Room(Vec3.ZERO, Vec3.ZERO);
		Room a = new Room(base, new Vec3(0, 0, -1), new Vec3(2, 1, 4), 0);
		a.createFourWalls();
		base.addChild(a);
		Room b = new Room(base, new Vec3(2.5, 2, -1), new Vec3(3, 3, 2), 0);
		b.createFourWalls();
		base.addChild(b);
		Gate gate = gateFactory.create(a, b);
		assertEquals(base, gate.getParent());
		assertEquals(new Vec3(1, 2, -1), gate.getCenter());
		assertEquals(new Vec2(2, 3), gate.getSize());
		assertEquals(-90, gate.getRotationY(), 0.0001);
	}
	
	@Test
	public void test1RoomRotated() {
		Room a = new Room(null, new Vec3(0, 0, -1), new Vec3(2, 1, 4), 0);
		a.createFourWalls();
		Room b = new Room(null, new Vec3(2.5, 2, -1), new Vec3(2, 3, 3), 90);
		b.createFourWalls();
		Gate gate = gateFactory.create(a, b);
		assertEquals(null, gate.getParent());
		assertEquals(new Vec3(1, 2, -1), gate.getCenter());
		assertEquals(new Vec2(2, 3), gate.getSize());
		assertEquals(-90, gate.getRotationY(), 0.0001);
	}
	
	@Test
	public void test1Rotated() {
		Room a = new Room(null, new Vec3(0, 0, -1), new Vec3(2, 1, 4), 0);
		a.createFourWalls();
		Room b = new Room(null, new Vec3(-2.5, 2, -1), new Vec3(3, 3, 2), 0);
		b.createFourWalls();
		Gate gate = gateFactory.create(a, b);
		assertEquals(new Vec3(-1, 2, -1), gate.getCenter());
		assertEquals(new Vec2(2, 3), gate.getSize());
		assertEquals(90, gate.getRotationY(), 0.0001);
	}
}
