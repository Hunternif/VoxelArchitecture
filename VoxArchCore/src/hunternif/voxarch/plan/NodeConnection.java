package hunternif.voxarch.plan;

import hunternif.voxarch.util.IntVec3;

/** Connects 2 NodeJoints. */
public abstract class NodeConnection extends Node {

	private NodeJoint start;
	private NodeJoint end;
	
	private IntVec3 endPoint;
	
	public NodeConnection(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}
	
	public NodeJoint getStart() {
		return start;
	}

	public void setStart(NodeJoint start) {
		this.start = start;
	}

	public NodeJoint getEnd() {
		return end;
	}

	public void setEnd(NodeJoint end) {
		this.end = end;
	}

	public IntVec3 getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(int x, int y, int z) {
		this.endPoint = new IntVec3(x, y, z);
	}

}
