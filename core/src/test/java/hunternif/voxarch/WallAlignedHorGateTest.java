package hunternif.voxarch;

import static org.junit.Assert.assertEquals;

import hunternif.voxarch.builder.BaseBuilderTest;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Structure;
import hunternif.voxarch.plan.gate.WallAlignedHorGateFactory;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.util.DebugUtil;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.IntVec3;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import org.junit.Test;

public class WallAlignedHorGateTest extends BaseBuilderTest {
	private IGateFactory gateFactory = new WallAlignedHorGateFactory();

	public WallAlignedHorGateTest() {
		super(10, 10, 10);
	}

	//================ Preliminary tests ================
	
	@Test
	public void noWalls() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(null, new Vec3(2, 1, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(1, 1, 0), gate.getCenter());
		assertEquals(new Vec2(3, 2), gate.getSize());
		assertEquals(90, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	@Test
	public void noWallsRotated() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getCenter().y, 0);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getCenter().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getCenter().z, 0.00001);
		assertEquals(new Vec2(3, 3), gate.getSize());
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.000001);
	}
	
	@Test
	public void wallsRotated() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getCenter().y, 0);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getCenter().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getCenter().z, 0.00001);
		assertEquals(new Vec2(3, 3), gate.getSize());
		// Aligned with wall of room2:
		assertEquals(270, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void wallsRotated2() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		room1.createFourWalls();
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(0, gate.getCenter().y, 0);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getCenter().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getCenter().z, 0.00001);
		assertEquals(new Vec2(3, 3), gate.getSize());
		// Aligned with wall of room1:
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	@Test
	public void largeRoomsNoWallsX() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(null, new Vec3(10, 40, 20), new Vec3(20, 100, 20), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 40, 10), gate.getCenter());
		assertEquals(new Vec2(20, 60), gate.getSize());
		assertEquals(0, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void largeRoomsNoWallsZ() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(null, new Vec3(20, 40, 10), new Vec3(20, 100, 20), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(10, 40, 0), gate.getCenter());
		assertEquals(new Vec2(20, 60), gate.getSize());
		assertEquals(90, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void largeRoomsWallsX() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(null, new Vec3(10, 40, 20), new Vec3(20, 100, 20), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 40, 10), gate.getCenter());
		assertEquals(new Vec2(20, 60), gate.getSize());
		assertEquals(180, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void largeRoomsWallsZ() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(null, new Vec3(20, 40, 5), new Vec3(20, 100, 20), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(10, 40, 0), gate.getCenter());
		assertEquals(new Vec2(20, 60), gate.getSize());
		assertEquals(270, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	//================== Generator tests ===================
	
	@Test
	public void diffRoomHeight1b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 4, 2));
		Room room2 = testRoom(new Vec3(4, 0, 1), new Vec3(2, 6, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2 2 2 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "0 0 0 2 2 2\n"
				+ "0 0 0 2 0 2\n"
				+ "0 0 0 2 2 2\n"
				+ "\n"
				+ "0 0 0 2 2 2\n"
				+ "0 0 0 2 3 2\n"
				+ "0 0 0 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 7, 3)));
	}
	
	@Test
	public void diffFloorHeight1b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 4, 2));
		Room room2 = testRoom(new Vec3(4, 2, 1), new Vec3(2, 4, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2 0 0 0\n"
				+ "2 1 2 0 0 0\n"
				+ "2 2 2 0 0 0\n"
				+ "\n"
				+ "2 2 2 0 0 0\n"
				+ "2 0 2 0 0 0\n"
				+ "2 2 2 0 0 0\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "0 0 0 2 2 2\n"
				+ "0 0 0 2 0 2\n"
				+ "0 0 0 2 2 2\n"
				+ "\n"
				+ "0 0 0 2 2 2\n"
				+ "0 0 0 2 3 2\n"
				+ "0 0 0 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 7, 3)));
	}
	
	@Test
	public void west1b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 3, 2));
		Room room2 = testRoom(new Vec3(4, 0, 1), new Vec3(2, 3, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2 2 2 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 3 2\n"
				+ "2 2 2 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 4, 3)));
	}
	
	@Test
	public void west2b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1.5), new Vec3(2, 3, 3));
		Room room2 = testRoom(new Vec3(4, 0, 1.5), new Vec3(2, 3, 3));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2 2 2 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 3 2\n"
				+ "2 3 2 2 3 2\n"
				+ "2 2 2 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 4, 4)));
	}
	
	@Test
	public void east1b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 3, 2));
		Room room2 = testRoom(new Vec3(4, 0, 1), new Vec3(2, 3, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room2, room1));
		build(root);
		
		String expected = ""
				+ "2 2 2 2 2 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 3 2\n"
				+ "2 2 2 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 4, 3)));
	}
	
	@Test
	public void east2b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1.5), new Vec3(2, 3, 3));
		Room room2 = testRoom(new Vec3(4, 0, 1.5), new Vec3(2, 3, 3));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room2, room1));
		build(root);
		
		String expected = ""
				+ "2 2 2 2 2 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 3 2\n"
				+ "2 3 2 2 3 2\n"
				+ "2 2 2 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 4, 4)));
	}
	
	@Test
	public void north1b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 3, 2));
		Room room2 = testRoom(new Vec3(1, 0, 4), new Vec3(2, 3, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room2, room1));
		build(root);
		
		String expected = ""
				+ "2 2 2\n"
				+ "2 1 2\n"
				+ "2 2 2\n"
				+ "2 2 2\n"
				+ "2 1 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 3 2\n"
				+ "2 2 2\n"
				+ "2 2 2\n"
				+ "2 3 2\n"
				+ "2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(3, 4, 6)));
	}
	
	@Test
	public void north2b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1.5, 0, 1), new Vec3(3, 3, 2));
		Room room2 = testRoom(new Vec3(1.5, 0, 4), new Vec3(3, 3, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room2, room1));
		build(root);
		
		String expected = ""
				+ "2 2 2 2\n"
				+ "2 1 1 2\n"
				+ "2 2 2 2\n"
				+ "2 2 2 2\n"
				+ "2 1 1 2\n"
				+ "2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2\n"
				+ "2 3 3 2\n"
				+ "2 2 2 2\n"
				+ "2 2 2 2\n"
				+ "2 3 3 2\n"
				+ "2 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(4, 4, 6)));
	}
	
	@Test
	public void south1b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1, 0, 1), new Vec3(2, 3, 2));
		Room room2 = testRoom(new Vec3(1, 0, 4), new Vec3(2, 3, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2\n"
				+ "2 1 2\n"
				+ "2 2 2\n"
				+ "2 2 2\n"
				+ "2 1 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 3 2\n"
				+ "2 2 2\n"
				+ "2 2 2\n"
				+ "2 3 2\n"
				+ "2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(3, 4, 6)));
	}
	
	@Test
	public void south2b() {
		Structure root = new Structure();
		Room room1 = testRoom(new Vec3(1.5, 0, 1), new Vec3(3, 3, 2));
		Room room2 = testRoom(new Vec3(1.5, 0, 4), new Vec3(3, 3, 2));
		root.addChild(room1);
		root.addChild(room2);
		root.addChild(new WallAlignedHorGateFactory().create(room1, room2));
		build(root);
		
		String expected = ""
				+ "2 2 2 2\n"
				+ "2 1 1 2\n"
				+ "2 2 2 2\n"
				+ "2 2 2 2\n"
				+ "2 1 1 2\n"
				+ "2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 0 0 2\n"
				+ "2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2\n"
				+ "2 3 3 2\n"
				+ "2 2 2 2\n"
				+ "2 2 2 2\n"
				+ "2 3 3 2\n"
				+ "2 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(4, 4, 6)));
	}
}
