package hunternif.voxarch.plan;

import hunternif.voxarch.util.IntVec3;

/** Base node of the architectural plan. */
public abstract class Node {
	private final ArchPlan plan;
	private final Floor floor;
	/** Points to the top left corner. Measured in blocks. */
	private IntVec3 origin;
	
	public Node(ArchPlan plan, Floor floor) {
		this.plan = plan;
		this.floor = floor;
		plan.addNode(this);
	}

	public Floor getFloor() {
		return floor;
	}

	public void setOrigin(IntVec3 origin) {
		this.origin = origin;
	}

	public IntVec3 getOrigin() {
		return origin;
	}

	public void setOrigin(int x, int y, int z) {
		this.origin = new IntVec3(x, y, z);
	}
	
	public Floor getNextFloor() {
		return new Floor(floor.getYLevel() + plan.getConfig().floorHeight());
	}
}
