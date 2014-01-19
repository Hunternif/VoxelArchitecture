package hunternif.voxarch.plan;

/** Floor on an architectural plan. Can have arbitrary y level. Corridor nodes
 * can only connect nodes on the same floor. Negative y level usually means
 * dungeon.
 */
public class Floor implements Comparable<Floor> {
	private final int yLevel;

	public Floor(int yLevel) {
		this.yLevel = yLevel;
	}
	
	public int getYLevel() {
		return yLevel;
	}
	
	@Override
	public int hashCode() {
		return yLevel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Floor)) return false;
		return ((Floor)obj).yLevel == yLevel;
	}
	
	@Override
	public String toString() {
		return "F" + yLevel;
	}

	@Override
	public int compareTo(Floor floor) {
		return yLevel - floor.yLevel;
	}
	
	public static Floor valueOf(int yLevel) {
		return new Floor(yLevel);
	}
}
