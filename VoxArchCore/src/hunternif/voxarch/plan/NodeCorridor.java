package hunternif.voxarch.plan;

/**
 * Connects 2 NodeJoints on the same floor.
 * @author Hunternif
 */
public class NodeCorridor extends NodeConnection {

	public NodeCorridor(ArchPlan plan, Floor floor) {
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
	
	/** Throws an IllegalArgmentException if the floors are not equal. */
	private static void checkFloors(NodeJoint start, NodeJoint end) {
		if (start != null && end != null && start.getFloor() != end.getFloor()) {
			throw new IllegalArgumentException(
					"Corridor cannot connect nodes on different floors. "
					+ "Start: " + start.getFloor() + " End: " + end.getFloor());
		}
	}

}
