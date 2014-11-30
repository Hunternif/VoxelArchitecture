package hunternif.voxarch;

import static org.junit.Assert.*;
import org.junit.Test;

import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.AlignedVerGateFactory;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class VerGateTest {
	private IGateFactory gateFactory = new AlignedVerGateFactory();
	
	@Test
	public void above() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3), 0);
		Room room2 = new Room(new Vec3(0, 2, 0), new Vec3(3, 2, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 2, 0), gate.getOrigin());
	}
	@Test
	public void below() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3), 0);
		Room room2 = new Room(new Vec3(0, -2, 0), new Vec3(3, 2, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 0, 0), gate.getOrigin());
	}
	
	@Test
	public void concentric() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(new Vec3(0, 2, 0), new Vec3(2, 2, 4), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getRotationY(), 0);
		assertEquals(new Vec3(0, 2.5, 0), gate.getOrigin());
		assertEquals(new Vec2(2, 3), gate.getSize());
	}
	@Test
	public void translated() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(new Vec3(2, 5, 0), new Vec3(2, 2, 4), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getRotationY(), 0);
		assertEquals(new Vec3(1.25, 4, 0), gate.getOrigin());
		assertEquals(new Vec2(0.5, 3), gate.getSize());
	}
	@Test
	public void rotated45() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3), 45);
		Room room2 = new Room(new Vec3(Math.sqrt(1.5*1.5*2), 2, 0), new Vec3(3, 2, 3), 45);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(45, gate.getRotationY(), 0);
		assertEquals(2, gate.getOrigin().y, 0);
		assertEquals(Math.sqrt(1.5*1.5*2)/2, gate.getOrigin().x, 0.000001);
		assertEquals(0, gate.getOrigin().z, 0.000001);
		assertEquals(1.5, gate.getSize().x, 0.000001);
		assertEquals(1.5, gate.getSize().y, 0.000001);
	}
	
	@Test
	public void noGate() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(new Vec3(4, 2, 0), new Vec3(2, 2, 4), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertNull(gate);
	}
}
