package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.util.RoomUtil;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import org.junit.Test;

/**
 * 
 * @author Hunternif
 *
 */
public class RoomTest {
	@Test
	public void testFourWalls() {
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 2, 4), 0);
		room.createFourWalls();
		assertEquals(4, room.getWalls().size());
		assertEquals(new Vec2(1, 1.5), room.getWalls().get(0).getP1());
		assertEquals(new Vec2(1, -1.5), room.getWalls().get(0).getP2());
		assertEquals(new Vec2(1, -1.5), room.getWalls().get(1).getP1());
		assertEquals(new Vec2(-1, -1.5), room.getWalls().get(1).getP2());
		assertEquals(new Vec2(-1, -1.5), room.getWalls().get(2).getP1());
		assertEquals(new Vec2(-1, 1.5), room.getWalls().get(2).getP2());
		assertEquals(new Vec2(-1, 1.5), room.getWalls().get(3).getP1());
		assertEquals(new Vec2(1, 1.5), room.getWalls().get(3).getP2());
	}
	
	@Test
	public void testFourRoundWalls() {
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 2, 4), 0);
		room.createRoundWalls(4);
		assertEquals(4, room.getWalls().size());
		assertEquals(new Vec2(1*MathUtil.cosDeg(-45), -1.5*MathUtil.sinDeg(-45)), room.getWalls().get(0).getP1());
		assertEquals(new Vec2(1*MathUtil.cosDeg(45), -1.5*MathUtil.sinDeg(45)), room.getWalls().get(0).getP2());
		assertEquals(new Vec2(1*MathUtil.cosDeg(45), -1.5*MathUtil.sinDeg(45)), room.getWalls().get(1).getP1());
		assertEquals(new Vec2(1*MathUtil.cosDeg(135), -1.5*MathUtil.sinDeg(135)), room.getWalls().get(1).getP2());
		assertEquals(new Vec2(1*MathUtil.cosDeg(135), -1.5*MathUtil.sinDeg(135)), room.getWalls().get(2).getP1());
		assertEquals(new Vec2(1*MathUtil.cosDeg(225), -1.5*MathUtil.sinDeg(225)), room.getWalls().get(2).getP2());
		assertEquals(new Vec2(1*MathUtil.cosDeg(225), -1.5*MathUtil.sinDeg(225)), room.getWalls().get(3).getP1());
		assertEquals(new Vec2(1*MathUtil.cosDeg(-45), -1.5*MathUtil.sinDeg(-45)), room.getWalls().get(3).getP2());
	}
	
	@Test
	public void testClosestWall() {
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 2, 4), 0);
		room.createFourWalls();
		assertEquals(room.getWalls().get(0), RoomUtil.findClosestWall(room, new Vec2(1, 0)));
		assertEquals(room.getWalls().get(0), RoomUtil.findClosestWall(room, new Vec2(1, 1.5)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(0.9, 1.6)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 0)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(-0.9, 1.5)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 0)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-2, 0)));
	}
	
	@Test
	public void testClosestRotatedWall() {
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(4, 2, 4), 45);
		room.createFourWalls();
		assertEquals(room.getWalls().get(0), RoomUtil.findClosestWall(room, new Vec2(1, -1)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(1, 1)));
		assertEquals(room.getWalls().get(1), RoomUtil.findClosestWall(room, new Vec2(-1, -1)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 1)));
	}
	
	@Test
	public void testHorGateNoWalls() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 0);
		Room room2 = new Room(null, new Vec3(2, 1, 0), new Vec3(3, 3, 3), 0);
		Gate gate = RoomUtil.createHorGateBetween(room1, room2);
		assertEquals(new Vec3(1, 1.5, 0), gate.getOrigin());
		assertEquals(new Vec2(3, 0.5), gate.getSize());
		assertEquals(90, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
	
	@Test
	public void testHorGateNoWallsRotated() {
		Room room1 = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 3, 3), 45);
		Room room2 = new Room(null, new Vec3(2, 0, 0), new Vec3(3, 3, 3), 0);
		Gate gate = RoomUtil.createHorGateBetween(room1, room2);
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
		Gate gate = RoomUtil.createHorGateBetween(room1, room2);
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
		Gate gate = RoomUtil.createHorGateBetween(room1, room2);
		assertEquals((0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().x, 0.00001);
		assertEquals(-(0.5 + 1.5/Math.sqrt(2))/2, gate.getOrigin().z, 0.00001);
		assertEquals(new Vec2(3, 1), gate.getSize());
		// Aligned with wall of room1:
		assertEquals(135, MathUtil.clampAngle(gate.getRotationY()), 0.0000001);
	}
}
