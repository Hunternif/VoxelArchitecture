package hunternif.voxarch;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import static org.junit.Assert.*;
import hunternif.voxarch.plan.Corridor;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

public class CorridorTest {
	//TODO: test corridor attaching to rooms with gates.
	
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
		assertEquals(new Vec3(1, 0, 0), gate.getCenter());
		assertEquals(new Vec2(2, 3), gate.getSize());
		assertEquals(90, gate.getRotationY(), 0.0001);
	}
	
	@Test
	public void endsBent45() {
		Room base = new Room(Vec3.ZERO, Vec3.ZERO);
		Room room = new Room(base, Vec3.ZERO, new Vec3(2, 1, 4), 0);
		room.createFourWalls();
		base.addChild(room);
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
		base.addChild(room);
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

	@Test
	public void complete1Straight() {
		Corridor cor = new Corridor(null, Vec3.ZERO, new Vec2(4, 3));
		cor.appendPoint(new Vec3(2, 0, 0));
		cor.build();
		assertEquals(1, cor.getChildren().size());
		Room r = cor.getRooms().get(0);
		assertEquals(new Vec3(1, 0, 0), r.getOrigin());
		assertEquals(new Vec3(2, 3, 4), r.getSize());
		assertEquals(0, r.getRotationY(), 0);
		assertEquals(new Vec2(1, 2), r.getWalls().get(0).getP1());
		assertEquals(new Vec2(1, -2), r.getWalls().get(0).getP2());
		assertEquals(new Vec2(1, -2), r.getWalls().get(1).getP1());
		assertEquals(new Vec2(-1, -2), r.getWalls().get(1).getP2());
		assertEquals(new Vec2(-1, -2), r.getWalls().get(2).getP1());
		assertEquals(new Vec2(-1, 2), r.getWalls().get(2).getP2());
		assertEquals(new Vec2(-1, 2), r.getWalls().get(3).getP1());
		assertEquals(new Vec2(1, 2), r.getWalls().get(3).getP2());
	}
	
	@Test
	public void complete90Turns() {
		Corridor cor = new Corridor(null, Vec3.ZERO, new Vec2(2, 3));
		cor.appendPoint(new Vec3(2, -2, 0));
		cor.appendPoint(new Vec3(2, 7, 3));
		cor.appendPoint(new Vec3(4, 0, 3));
		cor.build();
		Iterator<Room> iter = cor.getRooms().iterator();
		Room r = iter.next();
		assertEquals(new Vec3(1, -1, 0), r.getOrigin());
		assertEquals(new Vec3(4, 3, 2), r.getSize());
		assertEquals(0, r.getRotationY(), 0);
		assertEquals(new Vec2(0, 1), r.getWalls().get(0).getP1());
		assertEquals(new Vec2(2, -1), r.getWalls().get(0).getP2());
		assertEquals(new Vec2(2, -1), r.getWalls().get(1).getP1());
		assertEquals(new Vec2(-1, -1), r.getWalls().get(1).getP2());
		assertEquals(new Vec2(-1, -1), r.getWalls().get(2).getP1());
		assertEquals(new Vec2(-1, 1), r.getWalls().get(2).getP2());
		assertEquals(new Vec2(-1, 1), r.getWalls().get(3).getP1());
		assertEquals(new Vec2(0, 1), r.getWalls().get(3).getP2());
		r = iter.next();
		assertEquals(new Vec3(2, 2.5, 1.5), r.getOrigin());
		assertEquals(new Vec3(5, 3, 2), r.getSize());
		assertEquals(-90, r.getRotationY(), 0);
		assertEquals(new Vec2(2.5, 1), r.getWalls().get(0).getP1());
		assertEquals(new Vec2(0.5, -1), r.getWalls().get(0).getP2());
		assertEquals(new Vec2(0.5, -1), r.getWalls().get(1).getP1());
		assertEquals(new Vec2(-2.5, -1), r.getWalls().get(1).getP2());
		assertEquals(new Vec2(-2.5, -1), r.getWalls().get(2).getP1());
		assertEquals(new Vec2(-0.5, 1), r.getWalls().get(2).getP2());
		assertEquals(new Vec2(-0.5, 1), r.getWalls().get(3).getP1());
		assertEquals(new Vec2(2.5, 1), r.getWalls().get(3).getP2());
		r = iter.next();
		assertEquals(new Vec3(3, 3.5, 3), r.getOrigin());
		assertEquals(new Vec3(4, 3, 2), r.getSize());
		assertEquals(0, r.getRotationY(), 0);
		assertEquals(new Vec2(1, 1), r.getWalls().get(0).getP1());
		assertEquals(new Vec2(1, -1), r.getWalls().get(0).getP2());
		assertEquals(new Vec2(1, -1), r.getWalls().get(1).getP1());
		assertEquals(new Vec2(0, -1), r.getWalls().get(1).getP2());
		assertEquals(new Vec2(0, -1), r.getWalls().get(2).getP1());
		assertEquals(new Vec2(-2, 1), r.getWalls().get(2).getP2());
		assertEquals(new Vec2(-2, 1), r.getWalls().get(3).getP1());
		assertEquals(new Vec2(1, 1), r.getWalls().get(3).getP2());
		assertEquals(false, iter.hasNext());
	}
}
