package hunternif.voxarch.plan;

/** Connects 2 NodeJoints on the same floor. */
public class NodeCorridor extends NodeConnection {

	public NodeCorridor(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}
	
	@Override
	public void setStart(NodeJoint start) {
		NodeJoint end = getEnd();
		if (end != null && getEnd().getFloor() != start.getFloor()) {
			throw new IllegalArgumentException(
					"Corridor cannot connect nodes on different floors. "
					+ "Start: " + start.getFloor() + " End: " + end.getFloor());
		}
		super.setStart(start);
	}

	@Override
	public void setEnd(NodeJoint end) {
		NodeJoint start = getStart();
		if (start != null && end.getFloor() != start.getFloor()) {
			throw new IllegalArgumentException(
					"Corridor cannot connect nodes on different floors. "
					+ "Start: " + start.getFloor() + " End: " + end.getFloor());
		}
		super.setEnd(end);
	}

}
