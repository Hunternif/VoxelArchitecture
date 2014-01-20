package hunternif.voxarch.plan;

/**
 * A joint which is not a room, but an intersection or turn of a corridor.
 * @author Hunternif
 */
public class NodeCorridorJoint extends NodeJoint {

	public NodeCorridorJoint(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}

}
