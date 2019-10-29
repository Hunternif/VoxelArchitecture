package hunternif.voxarch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import hunternif.voxarch.plan.Node;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.vector.Matrix2;
import hunternif.voxarch.vector.Matrix4;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;
import hunternif.voxarch.vector.Vec4;

/**
 * 
 * @author Hunternif
 *
 */
public class RoomUtil {
	/**
	 * Returns the wall in the room that is closest to the specified point on
	 * the horizontal plane.
	 * In case of several walls being equally close (e.g. when the shortest
	 * distance to 2 walls is measured in their mutual corner), the 'closeness'
	 * is determined by comparing the average distance to their end points.
	 * @param room
	 * @param point coordinates relative to the parent of the room.
	 */
	public Wall findClosestWall(Room room, Vec2 point) {
		// Check if the room has no walls:
		if (room.getWalls().isEmpty()) return null;
		Vec2 p = new Vec2(point.x - room.getOrigin().x,
						  point.y - room.getOrigin().z);
		Matrix2 rot = Matrix2.rotationMatrix(room.getRotationY());
		rot.multiplyLocal(p);
		double distance = Double.MAX_VALUE;
		// First measure the actual distance:
		List<Wall> candidates = new ArrayList<>();
		for (Wall wall : room.getWalls()) {
			Vec2 dest = VectorUtil.closestPointOnLineSegment(p, wall.getP1(), wall.getP2());
			double curDistance = p.squareDistanceTo(dest);
			if (curDistance == distance) {
				candidates.add(wall);
			} else if (curDistance < distance) {
				distance = curDistance;
				candidates = new ArrayList<>();
				candidates.add(wall);
			}
		}
		if (candidates.size() > 1) {
			// Compare distances to their end points:
			//TODO: test the new improved RoomUtil.findClosestWall()
			Wall closest = null;
			distance = Double.MAX_VALUE;
			for (Wall wall : room.getWalls()) {
				double curDistance = p.squareDistanceTo(wall.getP1()) + p.squareDistanceTo(wall.getP2());
				if (curDistance < distance) {
					distance = curDistance;
					closest = wall;
				}
			}
			// At this point there still might be collisions, but whatever.
			// Your fault for designing a plan that causes such collisions :^)
			return closest;
		} else {
			return candidates.get(0);
		}
	}
	
	/**
	 * Traces a ray on the horizontal plane from the start point in the
	 * specified direction until it hits the bounding box of the room, the
	 * room's rotation considered.
	 * @param room		the target room.
	 * @param start		starting point of the ray.
	 * @param target	target of the ray, i.e. some point on the ray.
	 * @return			ray hit point on the room's bounding box
	 */
	public Vec2 rayTrace(Room room, Vec2 start, Vec2 target) {
		// Retrieve local coordinates of the corners of the room's bounding box:
		// (Minus angle, because this is the reference frame XZY is left-handed)
		Matrix2 rot = Matrix2.rotationMatrix(-room.getRotationY());
		Vec2 roomCenter = new Vec2(room.getOrigin().x, room.getOrigin().z);
		double halfWidth = room.getSize().x/2;
		double halfLength = room.getSize().z/2;
		Vec2 v1 = rot.multiplyLocal(new Vec2(halfWidth, halfLength)).addLocal(roomCenter);
		Vec2 v2 = rot.multiplyLocal(new Vec2(halfWidth, -halfLength)).addLocal(roomCenter);
		Vec2 v3 = rot.multiplyLocal(new Vec2(-halfWidth, -halfLength)).addLocal(roomCenter);
		Vec2 v4 = rot.multiplyLocal(new Vec2(-halfWidth, halfLength)).addLocal(roomCenter);
		Vec2[] vertices = {v1, v2, v3, v4, v1};
		
		// Find the closest intersection of the ray with a line segment [vN,vN+1]
		Vec2 intersection = null, vec = null;
		double distance = Double.MAX_VALUE, curDistance = 0;
		for (int i = 0; i < 4; i++) {
			vec = VectorUtil.rayTrace(start, target, vertices[i], vertices[i+1]);
			if (vec != null) {
				curDistance = vec.distanceTo(start);
				if (curDistance < distance) {
					distance = curDistance;
					intersection = vec;
				}
			}
		}
		return intersection;
	}
	
	/** WARNING: sub-optimal algorithm! */
	public Node findLowestCommonParent(Node roomA, Node roomB) {
		if (roomA == null || roomB == null) return null;
		if (roomA == roomB) return roomA;
		// Including this simple check because it is the most likely situation:
		if (roomA.getParent() == roomB.getParent()) return roomA.getParent();
		List<Node> ancestryA = new ArrayList<>();
		while (roomA != null) {
			ancestryA.add(roomA);
			roomA = roomA.getParent();
		}
		List<Node> ancestryB = new ArrayList<>();
		while (roomB != null) {
			ancestryB.add(roomB);
			roomB = roomB.getParent();
		}
		ListIterator<Node> iterA = ancestryA.listIterator(ancestryA.size());
		ListIterator<Node> iterB = ancestryB.listIterator(ancestryB.size());
		Node parent = null;
		while (iterA.hasPrevious() && iterB.hasPrevious()) {
			Node curParent = iterA.previous();
			if (curParent == iterB.previous()) {
				parent = curParent;
			} else {
				return parent;
			}
		}
		return parent;
	}
	
	private static class RoomPair {
		final Node from, to;
		public RoomPair(Node from, Node to) {
			this.from = from;
			this.to = to;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((to == null) ? 0 : to.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (!(obj instanceof RoomPair)) return false;
			RoomPair r = (RoomPair) obj;
			return r.from == from && r.to == to;
		}
	}
	/** For caching calls to translate* methods. */
	private Map<RoomPair, Matrix4> roomToRoomTranslatorCache = new HashMap<>();
	
	/**
	 * Converts the coordinates from local in-room to the room's immediate
	 * parent. The vector is not modified.
	 * <b>Only call this method none of the rooms' ancestors' position and
	 * orientation will be modified in future!</b> (Because caching)
	 */
	public Vec3 translateToParent(Node room, Vec3 local) {
		return Vec3.from(matrixTranslateToParent(room).multiplyLocal(Vec4.from(local)));
		
	}
	private Matrix4 matrixTranslateToParent(Node room) {
		RoomPair pairKey = new RoomPair(room, room.getParent());
		Matrix4 cached = roomToRoomTranslatorCache.get(pairKey);
		if (cached == null) {
			cached = Matrix4.translationAdd(room.getOrigin())
					.multiplyLocal(Matrix4.rotationY(room.getRotationY()));
			roomToRoomTranslatorCache.put(pairKey, cached);
		}
		return cached;
	}
	/**
	 * Converts the coordinates from the room's parent to local in-room.
	 * The vector is not modified.
	 * <b>Only call this method none of the rooms' ancestors' position and
	 * orientation will be modified in future!</b> (Because caching)
	 */
	public Vec3 translateToLocal(Node room, Vec3 external) {
		return Vec3.from(matrixTranslateToLocal(room).multiplyLocal(Vec4.from(external)));
	}
	private Matrix4 matrixTranslateToLocal(Node room) {
		RoomPair pairKey = new RoomPair(room.getParent(), room);
		Matrix4 cached = roomToRoomTranslatorCache.get(pairKey);
		if (cached == null) {
			cached = Matrix4.rotationY(-room.getRotationY()).multiplyLocal(
					Matrix4.translationSubtract(room.getOrigin()));
			roomToRoomTranslatorCache.put(pairKey, cached);
		}
		return cached;
	}
	
	/**
	 * Translate the coordinates local to one room, to local coordinates of
	 * another room.
	 * <b>Only call this method none of the rooms' ancestors' position and
	 * orientation will be modified in future!</b> (Because caching)
	 */
	public Vec3 translateToRoom(Node from, Vec3 local, Node to) {
		RoomPair pairKey = new RoomPair(from, to);
		Matrix4 cached = roomToRoomTranslatorCache.get(pairKey);
		if (cached != null) {
			return Vec3.from(cached.multiplyLocal(Vec4.from(local)));
		}
		List<Node> ancestryFrom = new ArrayList<>();
		while (from != null) {
			ancestryFrom.add(from);
			from = from.getParent();
		}
		List<Node> ancestryTo = new ArrayList<>();
		while (to != null) {
			ancestryTo.add(to);
			to = to.getParent();
		}
		ListIterator<Node> iterFrom = ancestryFrom.listIterator(ancestryFrom.size());
		ListIterator<Node> iterTo = ancestryTo.listIterator(ancestryTo.size());
		Node parent = null;
		while (iterFrom.hasPrevious() && iterTo.hasPrevious()) {
			Node curParent = iterFrom.previous();
			if (curParent == iterTo.previous()) {
				parent = curParent;
			} else {
				// We need this iterator to point to the child of the lowest common parent:
				if (iterTo.hasNext()) {
					iterTo.next();
				} else {
					// We only went in 1 step, gotta reset the iterator:
					iterTo = ancestryTo.listIterator(ancestryTo.size());
				}
				break;
			}
		}
		cached = Matrix4.identity();
		for (Node room : ancestryFrom) {
			if (room == parent) break;
			cached = matrixTranslateToParent(room).multiply(cached);
		}
		while (iterTo.hasPrevious()) {
			cached = matrixTranslateToLocal(iterTo.previous()).multiply(cached);
		}
		roomToRoomTranslatorCache.put(pairKey, cached);
		return Vec3.from(cached.multiplyLocal(Vec4.from(local)));
	}
	
}
