package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

/**
 * The architectural plan as a hierarchy of {@link Room}s.
 * @author Hunternif
 */
public class ArchPlan {

	/** The container for all structures in this plan. */
	private final Room base = new Room(null, new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);
	
	public Room getBase() {
		return base;
	}

}
