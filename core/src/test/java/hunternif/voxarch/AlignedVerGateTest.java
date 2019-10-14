package hunternif.voxarch;

import static org.junit.Assert.*;

import hunternif.voxarch.builder.BaseBuilderTest;
import hunternif.voxarch.plan.*;
import org.junit.Test;

import hunternif.voxarch.plan.gate.AlignedVerGateFactory;
import hunternif.voxarch.util.DebugUtil;
import hunternif.voxarch.vector.IntVec3;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class AlignedVerGateTest extends BaseBuilderTest {
	private AlignedVerGateFactory gateFactory = new AlignedVerGateFactory();

	public AlignedVerGateTest() {
		super(10, 10, 10);
	}

	@Test
	public void above() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3));
		Room room2 = new Room(new Vec3(0, 2, 0), new Vec3(3, 2, 3));
		Hatch gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 2, 0), gate.getCenter());
	}
	@Test
	public void below() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3));
		Room room2 = new Room(new Vec3(0, -2, 0), new Vec3(3, 2, 3));
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 0, 0), gate.getCenter());
	}
	
	@Test
	public void concentric() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3));
		Room room2 = new Room(new Vec3(0, 2, 0), new Vec3(2, 2, 4));
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getRotationY(), 0);
		assertEquals(new Vec3(0, 2.5, 0), gate.getCenter());
		assertEquals(new Vec2(2, 3), gate.getSize());
	}
	@Test
	public void translated() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3));
		Room room2 = new Room(new Vec3(2, 5, 0), new Vec3(2, 2, 4));
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getRotationY(), 0);
		assertEquals(new Vec3(1.25, 4, 0), gate.getCenter());
		assertEquals(new Vec2(0.5, 3), gate.getSize());
	}
	@Test
	public void rotated45() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 2, 3), 45);
		Room room2 = new Room(null, new Vec3(Math.sqrt(1.5*1.5*2), 2, 0), new Vec3(3, 2, 3), 45);
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(45, gate.getRotationY(), 0);
		assertEquals(2, gate.getCenter().y, 0);
		assertEquals(Math.sqrt(1.5*1.5*2)/2, gate.getCenter().x, 0.000001);
		assertEquals(0, gate.getCenter().z, 0.000001);
		assertEquals(1.5, gate.getSize().x, 0.000001);
		assertEquals(1.5, gate.getSize().y, 0.000001);
	}
	
	@Test
	public void noGate() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3));
		Room room2 = new Room(new Vec3(4, 2, 0), new Vec3(2, 2, 4));
        Hatch gate = gateFactory.create(room1, room2);
		assertNull(gate);
	}
	
	@Test
	public void generator() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 2, 2));
		Room room2 = testRoom(new Vec3(1, 3, 1), new Vec3(2, 2, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(gateFactory.create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2\n"
				+ "2 1 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 3 2\n"
				+ "2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(3, 6, 3)));
	}
}
