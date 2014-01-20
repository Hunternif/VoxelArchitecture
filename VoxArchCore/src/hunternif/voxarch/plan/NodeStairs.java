package hunternif.voxarch.plan;

/** Connects 2 NodeJoints on different floors. Multiple NodeStairs should
 * usually be grouped closely. */
public class NodeStairs extends NodeConnection {

	public NodeStairs(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}
	
	@Override
	public void setStart(NodeJoint start) {
		NodeJoint end = getEnd();
		if (end != null && end.getFloor() == start.getFloor()) {
			throw new IllegalArgumentException(
					"Corridor cannot connect nodes on the same floor: "
					+ start.getFloor());
		}
		super.setStart(start);
	}

	@Override
	public void setEnd(NodeJoint end) {
		NodeJoint start = getStart();
		if (start != null && end.getFloor() == start.getFloor()) {
			throw new IllegalArgumentException(
					"Corridor cannot connect nodes on the same floor: "
					+ start.getFloor());
		}
		super.setEnd(end);
	}

}
