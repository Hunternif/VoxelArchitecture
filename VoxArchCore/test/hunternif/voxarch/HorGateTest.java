package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.WallAlignedHorGateFactory;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.util.DebugUtil;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.vector.IntVec3;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import org.junit.Test;

public class HorGateTest extends GeneratorTest {
	private IGateFactory gateFactory = new WallAlignedHorGateFactory();
	
	//================ Preliminary tests ================
	
	@Test
	public void noWalls() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(new Vec3(2, 1, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(1, 1.5, 0), gate.getOrigin());
		assertEquals(new Vec2(3, 0.5), gate.getSize());
		assertEquals(90, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	@Test
	public void noWallsRotated() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(1, gate.getOrigin().y, 0);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.000001);
	}
	
	@Test
	public void wallsRotated() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		// Aligned with wall of room2:
		assertEquals(270, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void wallsRotated2() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		// Aligned with wall of room1:
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	@Test
	public void largeRoomsNoWallsX() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(new Vec3(10, 40, 20), new Vec3(20, 100, 20), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 21, 10), gate.getOrigin());
		assertEquals(new Vec2(20, 78), gate.getSize());
		assertEquals(0, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void largeRoomsNoWallsZ() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(new Vec3(20, 40, 10), new Vec3(20, 100, 20), 0);
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(10, 21, 0), gate.getOrigin());
		assertEquals(new Vec2(20, 78), gate.getSize());
		assertEquals(90, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void largeRoomsWallsX() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(new Vec3(10, 40, 20), new Vec3(20, 100, 20), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(0, 21, 10), gate.getOrigin());
		assertEquals(new Vec2(20, 78), gate.getSize());
		assertEquals(180, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	@Test
	public void largeRoomsWallsZ() {
		Room room1 = new Room(new Vec3(0, 0, 0), new Vec3(20, 100, 20), 0);
		Room room2 = new Room(new Vec3(20, 40, 5), new Vec3(20, 100, 20), 0);
		room2.createFourWalls();
		Gate gate = gateFactory.create(room1, room2);
		assertEquals(new Vec3(10, 21, 0), gate.getOrigin());
		assertEquals(new Vec2(20, 78), gate.getSize());
		assertEquals(270, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	//================== Generator tests ===================
	
	@Test
	public void diffRoomHeight1b() {
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 5, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 0, 1), new Vec3(3, 7, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 6, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 2, 1), new Vec3(3, 6, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 8, 3)));
	}
	
	@Test
	public void west1b() {
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 0, 1), new Vec3(3, 4, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 4, 4), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 0, 1), new Vec3(3, 4, 4), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 0, 1), new Vec3(3, 4, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room2, room1));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 4, 4), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 0, 1), new Vec3(3, 4, 4), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room2, room1));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(1, 0, 4), new Vec3(3, 4, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room2, room1));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(4, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(1, 0, 4), new Vec3(4, 4, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room2, room1));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(1, 0, 4), new Vec3(3, 4, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(4, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(1, 0, 4), new Vec3(4, 4, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
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
