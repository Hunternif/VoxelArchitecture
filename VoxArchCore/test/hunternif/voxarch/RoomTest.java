package hunternif.voxarch;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import static org.junit.Assert.*;
import org.junit.Test;

public class RoomTest {
	@Test
	public void testFourWalls() {
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(2, 2, 3), 0);
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
		Room room = new Room(null, new Vec3(0, 0, 0), new Vec3(2, 2, 3), 0);
		room.createRoundWalls(4);
		assertEquals(4, room.getWalls().size());
		assertEquals(new Vec2(1, 0), room.getWalls().get(0).getP1());
		assertEquals(new Vec2(0, -1.5), room.getWalls().get(0).getP2());
		assertEquals(new Vec2(0, -1.5), room.getWalls().get(1).getP1());
		assertEquals(new Vec2(-1, 0), room.getWalls().get(1).getP2());
		assertEquals(new Vec2(-1, 0), room.getWalls().get(2).getP1());
		assertEquals(new Vec2(0, 1.5), room.getWalls().get(2).getP2());
		assertEquals(new Vec2(0, 1.5), room.getWalls().get(3).getP1());
		assertEquals(new Vec2(1, 0), room.getWalls().get(3).getP2());
	}
}
