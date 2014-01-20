package hunternif.voxarch.plan;

/** Generic room, can be connected to several corridors. */
public class NodeRoom extends NodeJoint {

	private int widthInCells;
	private int lengthInCells;
	
	public NodeRoom(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}
	
	public int getWidthInCells() {
		return widthInCells;
	}

	public void setWidthInCells(int widthInCells) {
		this.widthInCells = widthInCells;
	}

	public int getLengthInCells() {
		return lengthInCells;
	}

	public void setLengthInCells(int lengthInCells) {
		this.lengthInCells = lengthInCells;
	}

}
