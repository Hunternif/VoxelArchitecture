package hunternif.voxarch.util;

import hunternif.voxarch.vector.Vec2;

/**
 * 
 * @author Hunternif
 *
 */
public class VectorUtil {
	/**
	 * Returns the point on a line segment closest to the specified point.
	 * @param from	the point
	 * @param a		start of the line segment
	 * @param b		end of the line segment
	 */
	public static Vec2 closestPointOnLineSegment(Vec2 from, Vec2 a, Vec2 b) {
		Vec2 segVec = new Vec2(b.x - a.x, b.y - a.y);
		double t = new Vec2(from).subtract(a).dotProduct(segVec) / segVec.dotProduct(segVec);
		// We're measuring distance to the line segment, taking into account
		// its finite length, therefore we constrain how far the orthogonally
		// projected point can go along the line:
		if (t < 0) t = 0;
		if (t > 1) t = 1;
		Vec2 dest = new Vec2(a).add(segVec.multiply(t));
		return dest;
	}
	
	/**
	 * Returns the point on a line segment as a result of its intersection with a ray.
	 * @param rayStart	starting point of the ray
	 * @param rayTarget	point on a ray, defining its direction
	 * @param a			start of the line segment
	 * @param b			end of the line segment
	 * @return			the intersection point or null, if they don't intersect.
	 */
	public static Vec2 rayTrace(Vec2 rayStart, Vec2 rayTarget, Vec2 a, Vec2 b) {
		Vec2 lineVec = new Vec2(b).subtract(a);
		Vec2 ray = new Vec2(rayTarget).subtract(rayStart);
		double determinant = lineVec.x * ray.y - lineVec.y * ray.x;
		if (determinant == 0) {
			// The ray and the line are parallel
			return null;
		}
		double t = (ray.x * (a.y - rayStart.y) - ray.y * (a.x - rayStart.x)) / determinant;
		if (t < 0 || t > 1) {
			// Not intersecting
			return null;
		}
		return new Vec2(a).add(lineVec.multiply(t));
	}
}
