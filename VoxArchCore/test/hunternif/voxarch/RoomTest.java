package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
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
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(2, 1, 3), 0);
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
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(2, 1, 3), 0);
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
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(2, 1, 3), 0);
		room.createFourWalls();
		assertEquals(room.getWalls().get(0), RoomUtil.findClosestWall(room, new Vec2(1, 0)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(1, 1.5)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(0.9, 1.6)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 0)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(-0.9, 1.5)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 0)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-2, 0)));
	}
	
	@Test
	public void testClosestRotatedWall() {
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(3, 1, 3), 45);
		room.createFourWalls();
		assertEquals(room.getWalls().get(0), RoomUtil.findClosestWall(room, new Vec2(1, -1)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(1, 1)));
		assertEquals(room.getWalls().get(1), RoomUtil.findClosestWall(room, new Vec2(-1, -1)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 1)));
	}
}
