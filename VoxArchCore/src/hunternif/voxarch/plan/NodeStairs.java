package hunternif.voxarch.plan;

/**
 * Connects 2 NodeJoints on different floors. Multiple NodeStairs should
 * usually be grouped closely.
 * @author Hunternif
 */
public class NodeStairs extends NodeConnection {

	public NodeStairs(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}
	
	@Override
	public void setStart(NodeJoint start) {
		checkFloors(start, getEnd());
		super.setStart(start);
	}

	@Override
	public void setEnd(NodeJoint end) {
		checkFloors(getStart(), end);
		super.setEnd(end);
	}
	
	/** Throws an IllegalArgmentException if the floors are equal. */
	private static void checkFloors(NodeJoint start, NodeJoint end) {
		if (start != null && end != null && start.getFloor() == end.getFloor()) {
			throw new IllegalArgumentException(
					"Stairs cannot connect nodes on the same floor: "
					+ start.getFloor());
		}
	}

}
