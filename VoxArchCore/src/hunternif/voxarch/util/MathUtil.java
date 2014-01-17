package hunternif.voxarch.util;

public class MathUtil {
	/** Returns the closest int to the argument, with ties rounding up. */
	public static int roundUp(float a) {
		return Math.round(a);
	}
	
	/** Returns the closest int to the argument, with ties rounding down. */
	public static int roundDown(float a) {
		float r = a % 1;
		if (r < 0) r += 1;
		return r == 0.5f ? Math.round(a) - 1 : Math.round(a);
	}
	
	/** Clamps the angle in the interval [0, 360), performing 360*N shift if needed. */
	public static float clampAngle(float angle) {
		if (angle >= 0) return angle % 360;
		return 360 * ceiling(- angle / 360) + angle;
	}
	
	public static int ceiling(float a) {
		int ceil = (int) a;
		return a > (float) ceil ? ceil + 1 : ceil;
	}
}
