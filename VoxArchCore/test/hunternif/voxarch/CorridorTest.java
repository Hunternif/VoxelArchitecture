package hunternif.voxarch;

import java.util.LinkedList;

import org.junit.Test;

import static org.junit.Assert.*;
import hunternif.voxarch.plan.Corridor;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class CorridorTest {

	private static class CorridorForTesting extends Corridor {
		public CorridorForTesting(Room parent, Vec3 origin, Vec2 sectionSize) {
			super(parent, origin, sectionSize);
		}
		public LinkedList<Vec3> getPath() {
			return this.path;
		}
		@Override
		public Vec3 findPointOnNormalToWall(Room room, Vec3 first,
				Vec3 second, boolean addGate) {
			return super.findPointOnNormalToWall(room, first, second, addGate);
		}
		@Override
		public void buildEnvelopes() {
			super.buildEnvelopes();
		}
		public Vec3[] getEnvelopeLeft() {
			return envelopeLeft;
		}
		public Vec3[] getEnvelopeRight() {
			return envelopeRight;
		}
	}
	
	@Test
	public void endsStraight() {
		Room base = new Room(Vec3.ZERO, Vec3.ZERO);
		Room room = new Room(base, Vec3.ZERO, new Vec3(2, 1, 4), 0);
		room.createFourWalls();
		CorridorForTesting cor = new CorridorForTesting(base, new Vec3(1, 0, 0), new Vec2(2, 3));
		cor.appendPoint(new Vec3(4, 2, 0));
		Vec3 point = cor.findPointOnNormalToWall(room, cor.getPath().getFirst(), cor.getPath().get(1), true);
		assertEquals(new Vec3(4, 0, 0), point);
		Gate gate = base.getGates().get(0);
		assertEquals(new Vec3(1, 0, 0), gate.getOrigin());
		assertEquals(new Vec2(2, 3), gate.getSize());
		assertEquals(90, gate.getRotationY(), 0.0001);
	}
	
	@Test
	public void endsBent45() {
		Room base = new Room(Vec3.ZERO, Vec3.ZERO);
		Room room = new Room(base, Vec3.ZERO, new Vec3(2, 1, 4), 0);
		room.createFourWalls();
		CorridorForTesting cor = new CorridorForTesting(base, new Vec3(1, 0, 0), new Vec2(2, 3));
		cor.appendPoint(new Vec3(2, 0, 1));
		Vec3 point = cor.findPointOnNormalToWall(room, cor.getPath().getFirst(), cor.getPath().get(1), false);
		assertEquals(new Vec3(1, 0, 0), point);
	}
	
	@Test
	public void endsBent90() {
		Room base = new Room(Vec3.ZERO, Vec3.ZERO);
		Room room = new Room(base, Vec3.ZERO, new Vec3(2, 1, 4), 0);
		room.createFourWalls();
		CorridorForTesting cor = new CorridorForTesting(base, new Vec3(1, 0, 0), new Vec2(2, 3));
		cor.appendPoint(new Vec3(1, 0, 2));
		Vec3 point = cor.findPointOnNormalToWall(room, cor.getPath().getFirst(), cor.getPath().get(1), false);
		assertEquals(new Vec3(1, 0, 0), point);
	}
	
	@Test
	public void envelopeStraight() {
		CorridorForTesting cor = new CorridorForTesting(null, Vec3.ZERO, new Vec2(2, 2));
		cor.getPath().add(new Vec3(1, -2, 0));
		cor.getPath().add(new Vec3(3, 7, 0));
		cor.getPath().add(new Vec3(4, 9, 0));
		cor.getPath().add(new Vec3(5, 0, 0));
		cor.buildEnvelopes();
		Vec3[] expectedEnvelopeLeft = {
				new Vec3(0, 0, -1),
				new Vec3(1, -2, -1),
				new Vec3(3, 7, -1),
				new Vec3(4, 9, -1),
				new Vec3(5, 0, -1)
		};
		Vec3[] expectedEnvelopeRight = {
				new Vec3(0, 0, 1),
				new Vec3(1, -2, 1),
				new Vec3(3, 7, 1),
				new Vec3(4, 9, 1),
				new Vec3(5, 0, 1)
		};
		assertArrayEquals(expectedEnvelopeLeft, cor.getEnvelopeLeft());
		assertArrayEquals(expectedEnvelopeRight, cor.getEnvelopeRight());
	}
	
	@Test
	public void envelope90Turns() {
		CorridorForTesting cor = new CorridorForTesting(null, Vec3.ZERO, new Vec2(2, 2));
		cor.getPath().add(new Vec3(2, -2, 0));
		cor.getPath().add(new Vec3(2, 7, 3));
		cor.getPath().add(new Vec3(5, 9, 3));
		cor.getPath().add(new Vec3(5, 0, 2));
		cor.getPath().add(new Vec3(8, 4, 2));
		cor.buildEnvelopes();
		Vec3[] expectedEnvelopeLeft = {
				new Vec3(0, 0, -1),
				new Vec3(3, -2, -1),
				new Vec3(3, 7, 2),
				new Vec3(4, 9, 2),
				new Vec3(4, 0, 1),
				new Vec3(8, 4, 1)
		};
		Vec3[] expectedEnvelopeRight = {
				new Vec3(0, 0, 1),
				new Vec3(1, -2, 1),
				new Vec3(1, 7, 4),
				new Vec3(6, 9, 4),
				new Vec3(6, 0, 3),
				new Vec3(8, 4, 3)
		};
		assertArrayEquals(expectedEnvelopeLeft, cor.getEnvelopeLeft());
		assertArrayEquals(expectedEnvelopeRight, cor.getEnvelopeRight());
	}

}
