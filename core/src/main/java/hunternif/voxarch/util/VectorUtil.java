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
		double t = new Vec2(from).subtractLocal(a).dotProduct(segVec) / segVec.dotProduct(segVec);
		// We're measuring distance to the line segment, taking into account
		// its finite length, therefore we constrain how far the orthogonally
		// projected point can go along the line:
		if (t < 0) t = 0;
		if (t > 1) t = 1;
		Vec2 dest = new Vec2(a).addLocal(segVec.multiplyLocal(t));
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
		Vec2 lineVec = new Vec2(b).subtractLocal(a);
		Vec2 ray = new Vec2(rayTarget).subtractLocal(rayStart);
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
		Vec2 intersection = new Vec2(a).addLocal(lineVec.multiplyLocal(t));
		
		// So far we have found the intersection of the LINE of the ray with the
		// segment. We must eliminate the case where this intersection happens
		// on the point of the LINE in the opposite direction of the ray.
		if ((intersection.x - rayStart.x)*(rayTarget.x - rayStart.x) >= 0 &&
				(intersection.y - rayStart.y)*(rayTarget.y - rayStart.y) >= 0) {
			return intersection;
		} else {
			// Gotcha!
			return null;
		}
	}
}
