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
	public void fourWalls() {
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(2, 1, 3));
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
	public void fourRoundWalls() {
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(2, 1, 3));
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
	public void closestWall() {
		Room room = new Room(new Vec3(0, 0, 0), new Vec3(2, 1, 3));
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
	public void closestRotatedWall() {
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(3, 1, 3), 45);
		room.createFourWalls();
		assertEquals(room.getWalls().get(0), RoomUtil.findClosestWall(room, new Vec2(1, -1)));
		assertEquals(room.getWalls().get(3), RoomUtil.findClosestWall(room, new Vec2(1, 1)));
		assertEquals(room.getWalls().get(1), RoomUtil.findClosestWall(room, new Vec2(-1, -1)));
		assertEquals(room.getWalls().get(2), RoomUtil.findClosestWall(room, new Vec2(-1, 1)));
	}
	
	@Test
	public void lowestCommonParent() {
		Room a = childOf(null);
		assertEquals(a, RoomUtil.findLowestCommonParent(a, a));
		Room b = childOf(null);
		assertEquals(null, RoomUtil.findLowestCommonParent(a, b));
		a.addChild(b);
		assertEquals(a, RoomUtil.findLowestCommonParent(a, b));
		a.removeChild(b);
		assertEquals(null, RoomUtil.findLowestCommonParent(a, b));
		a.addChild(b);
		Room c = childOf(null);
		Room d = childOf(c);
		Room e = childOf(d);
		d.addChild(a);
		assertEquals(d, RoomUtil.findLowestCommonParent(a, e));
		assertEquals(d, RoomUtil.findLowestCommonParent(b, e));
		Room f = childOf(c);
		assertEquals(c, RoomUtil.findLowestCommonParent(a, f));
		c.removeChild(d);
		assertEquals(null, RoomUtil.findLowestCommonParent(a, f));
	}
	
	@Test
	public void translateCoordinates() {
		Room room = new Room(new Vec3(3, 1, 0), new Vec3(2, 1, 4));
		assertEquals(new Vec3(3, 1, 0), RoomUtil.translateToParent(room, new Vec3(0, 0, 0)));
		assertEquals(new Vec3(4, 3, 2), RoomUtil.translateToParent(room, new Vec3(1, 2, 2)));
		assertEquals(new Vec3(1, 0, -1), RoomUtil.translateToParent(room, new Vec3(-2, -1, -1)));
		
		assertEquals(new Vec3(0, 0, 0), RoomUtil.translateToLocal(room, new Vec3(3, 1, 0)));
		assertEquals(new Vec3(1, 2, 2), RoomUtil.translateToLocal(room, new Vec3(4, 3, 2)));
		assertEquals(new Vec3(-2, -1, -1), RoomUtil.translateToLocal(room, new Vec3(1, 0, -1)));
	}
	@Test
	public void translateCoordinatesRotated() {
		Room room = new Room(null, new Vec3(3, 1, 0), new Vec3(2, 1, 4), 90);
		assertEquals(new Vec3(3, 1, 0), RoomUtil.translateToParent(room, new Vec3(0, 0, 0)));
		assertEquals(new Vec3(5, 3, -1), RoomUtil.translateToParent(room, new Vec3(1, 2, 2)));
		assertEquals(new Vec3(2, 0, 2), RoomUtil.translateToParent(room, new Vec3(-2, -1, -1)));
		
		assertEquals(new Vec3(0, 0, 0), RoomUtil.translateToLocal(room, new Vec3(3, 1, 0)));
		assertEquals(new Vec3(1, 2, 2), RoomUtil.translateToLocal(room, new Vec3(5, 3, -1)));
		assertEquals(new Vec3(-2, -1, -1), RoomUtil.translateToLocal(room, new Vec3(2, 0, 2)));
	}
	@Test
	public void translateCoords2Way() {
		// See if equal translating back and forth produces the same result:
		Room a = new Room(new Vec3(123, -345, 346), Vec3.ZERO);
		Room b = new Room(a, new Vec3(57, 13, -56), Vec3.ZERO, 90);
		Room c = new Room(a, new Vec3(57, 13, -56), Vec3.ZERO, 90);
		Vec3 vec = new Vec3(-35, 57, 51);
		assertEquals(vec, RoomUtil.translateToRoom(a, vec, a));
		assertEquals(vec, RoomUtil.translateToRoom(b, vec, b));
		assertEquals(vec, RoomUtil.translateToRoom(a, vec, a)); // cached
		assertEquals(vec, RoomUtil.translateToRoom(b, vec, c));
		
		// Now for the actual coordinate test:
		a = new Room(new Vec3(1, 0, 1), Vec3.ZERO);
		b = new Room(a, new Vec3(1, 1, 1), Vec3.ZERO, 0);
		c = new Room(a, new Vec3(1, 1, 1), Vec3.ZERO, 90);
		assertEquals(new Vec3(3, 1, 3), RoomUtil.translateToRoom(b, new Vec3(1, 0, 1), null));
		assertEquals(new Vec3(1, 0, 1), RoomUtil.translateToRoom(null, new Vec3(3, 1, 3), b));
		assertEquals(new Vec3(2, 1, 2), RoomUtil.translateToParent(b, new Vec3(1, 0, 1)));
		assertEquals(new Vec3(2, 1, 0), RoomUtil.translateToParent(c, new Vec3(1, 0, 1)));
		assertEquals(new Vec3(1, 1, 1), RoomUtil.translateToParent(c, new Vec3(0, 0, 0)));
		assertEquals(new Vec3(2, 1, 2), RoomUtil.translateToParent(a, new Vec3(1, 1, 1)));
		assertEquals(RoomUtil.translateToRoom(c, new Vec3(0, 0, 0), null),
				RoomUtil.translateToParent(a, RoomUtil.translateToParent(c, new Vec3(0, 0, 0))));
		assertEquals(RoomUtil.translateToRoom(c, new Vec3(45, 234, -23), null),
				RoomUtil.translateToParent(a, RoomUtil.translateToParent(c, new Vec3(45, 234, -23))));
		assertEquals(new Vec3(3, 1, 1), RoomUtil.translateToRoom(c, new Vec3(1, 0, 1), null));
	}
	
	/** Helper method. */
	public static Room childOf(Room parent) {
		return new Room(parent, Vec3.ZERO, Vec3.ZERO, 0);
	}
}
