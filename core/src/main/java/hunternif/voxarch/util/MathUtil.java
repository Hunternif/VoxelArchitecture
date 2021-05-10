package hunternif.voxarch.util;

/**
 * Math helper methods.
 * @author Hunternif
 */
public class MathUtil {
	private static final double SIN_TABLE_LENGTH_OVER_360 = 65536d / 360d;
	
	/** Pre-calculated Sin values in degrees. */
	private static double[] SIN_TABLE_DEG = new double[65536];
	static {
		for (int i = 0; i < 65536; ++i)
			SIN_TABLE_DEG[i] = Math.sin((double)i / 65536d * 2 * Math.PI);

		SIN_TABLE_DEG[0] = 0;
		SIN_TABLE_DEG[16384] = 1;
		SIN_TABLE_DEG[32768] = 0;
		SIN_TABLE_DEG[49152] = -1;
	}
	
	public static double sinDeg(double degrees) {
		return SIN_TABLE_DEG[(int) (degrees * SIN_TABLE_LENGTH_OVER_360) & 65535];
	}

	public static double cosDeg(double degrees) {
		return SIN_TABLE_DEG[(int) ((degrees + 90) * SIN_TABLE_LENGTH_OVER_360) & 65535];
	}

	public static double atan2Deg(double y, double x) {
		return Math.atan2(y, x) * 180 / Math.PI;
	}
	
	/** Returns the closest int to the argument, with ties rounding up. */
	public static int roundUp(double a) {
		return (int)Math.round(a);
	}
	/** Returns the closest int to the argument, with ties rounding up. */
	public static int roundUp(float a) {
		return Math.round(a);
	}
	
	/** Returns the closest int to the argument, with ties rounding down. */
	public static int roundDown(double a) {
		double r = a % 1;
		if (r < 0) r += 1;
		return r == 0.5 ? (int)Math.round(a) - 1 : (int)Math.round(a);
	}
	/** Returns the closest int to the argument, with ties rounding down. */
	public static int roundDown(float a) {
		float r = a % 1;
		if (r < 0) r += 1;
		return r == 0.5f ? Math.round(a) - 1 : Math.round(a);
	}
	
	/** Clamps the angle in the interval [0, 360), performing 360*N shift if needed. */
	public static double clampAngle(double angle) {
		if (angle >= 0) return angle % 360;
		return 360 * ceiling(- angle / 360) + angle;
	}
	
	public static int floor(double a) {
		int floor = (int) a;
		return a < (double) floor ? floor - 1 : floor;
	}
	
	public static int ceiling(float a) {
		int ceil = (int) a;
		return a > (float) ceil ? ceil + 1 : ceil;
	}
	public static int ceiling(double a) {
		int ceil = (int) a;
		return a > (double) ceil ? ceil + 1 : ceil;
	}
	/** Returns ceiling for absolute value of a, keeping the sign. */
	public static int ceilingAbs(double a) {
		int ceil = (int) a;
		if (a >= 0) {
			return a > (double) ceil ? ceil + 1 : ceil;
		} else {
			return a < (double) ceil ? ceil - 1 : ceil;
		}
	}
	
	public static double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}
	
	public static double min(double a, double b, double c, double d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
	}
}
