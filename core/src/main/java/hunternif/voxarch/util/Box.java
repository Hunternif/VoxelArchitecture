package hunternif.voxarch.util;

import hunternif.voxarch.vector.Vec3;

/**
 * Axis-aligned box.
 * @author Hunternif
 */
public class Box {
	public final double minX, maxX, minY, maxY, minZ, maxZ;
	
	public Box(double minX, double maxX,
						 double minY, double maxY,
						 double minZ, double maxZ) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
		if (minX > maxX || minY > maxY || minZ > maxZ) {
			throw new IllegalArgumentException("Negative box size: " + this.toString());
		}
	}
	
	public Box(Vec3 center, Vec3 size) {
		this(center.x - size.x/2, center.x + size.x/2,
			 center.y, center.y + size.y,
			 center.z - size.z/2, center.z + size.z/2);
	}

	public static Box fromCorners(Vec3 start, Vec3 end) {
		return new Box(start.x, end.x, start.y, end.y, start.z, end.z);
	}
	
	@Override
	public String toString() {
		return String.format("{[%s,%s][%s,%s][%s,%s]}", minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Box)) return false;
		Box box = (Box) obj;
		return minX == box.minX && maxX == box.maxX &&
			   minY == box.minY && maxY == box.maxY &&
			   minZ == box.minZ && maxZ == box.maxZ;
	}
	
	/** See {@link Box#intersect(Box, Box)} */
	public Box intersect(Box box) {
		return Box.intersect(this, box);
	}
	
	/** Returns the box spanning the volume of intersection of the 2 boxes,
	 * or null if they don't intersect. */ 
	public static Box intersect(Box box1, Box box2) {
		double minX = Math.max(box1.minX, box2.minX);
		double maxX = Math.min(box1.maxX, box2.maxX);
		double minY = Math.max(box1.minY, box2.minY);
		double maxY = Math.min(box1.maxY, box2.maxY);
		double minZ = Math.max(box1.minZ, box2.minZ);
		double maxZ = Math.min(box1.maxZ, box2.maxZ);
		if (minX >= maxX || minY >= maxY || minZ >= maxZ) {
			return null;
		} else {
			return new Box(minX, maxX, minY, maxY, minZ, maxZ);
		}
	}
}
