package hunternif.voxarch;

import static org.junit.Assert.*;

import hunternif.voxarch.plan.Hatch;
import org.junit.Test;

import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.AlignedVerGateFactory;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.util.DebugUtil;
import hunternif.voxarch.vector.IntVec3;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class AlignedVerGateTest extends GeneratorTest {
	private AlignedVerGateFactory gateFactory = new AlignedVerGateFactory();
	
	@Test
	public void above() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3));
		Room room2 = new Room(new Vec3(0, 2, 0), new Vec3(3, 2, 3));
		Hatch gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 2, 0), gate.getOrigin());
	}
	@Test
	public void below() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 2, 3));
		Room room2 = new Room(new Vec3(0, -2, 0), new Vec3(3, 2, 3));
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 0, 0), gate.getOrigin());
	}
	
	@Test
	public void concentric() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3));
		Room room2 = new Room(new Vec3(0, 2, 0), new Vec3(2, 2, 4));
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getRotationY(), 0);
		assertEquals(new Vec3(0, 2.5, 0), gate.getOrigin());
		assertEquals(new Vec2(2, 3), gate.getSize());
	}
	@Test
	public void translated() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3));
		Room room2 = new Room(new Vec3(2, 5, 0), new Vec3(2, 2, 4));
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getRotationY(), 0);
		assertEquals(new Vec3(1.25, 4, 0), gate.getOrigin());
		assertEquals(new Vec2(0.5, 3), gate.getSize());
	}
	@Test
	public void rotated45() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 2, 3), 45);
		Room room2 = new Room(null, new Vec3(Math.sqrt(1.5*1.5*2), 2, 0), new Vec3(3, 2, 3), 45);
        Hatch gate = gateFactory.create(room1, room2);
		assertEquals(45, gate.getRotationY(), 0);
		assertEquals(2, gate.getOrigin().y, 0);
		assertEquals(Math.sqrt(1.5*1.5*2)/2, gate.getOrigin().x, 0.000001);
		assertEquals(0, gate.getOrigin().z, 0.000001);
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
		ArchPlan plan = new ArchPlan();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 2, 2));
		Room room2 = testRoom(new Vec3(1, 3, 1), new Vec3(2, 2, 2));
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addChild(gateFactory.create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
