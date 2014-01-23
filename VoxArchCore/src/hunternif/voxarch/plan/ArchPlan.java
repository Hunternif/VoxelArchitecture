package hunternif.voxarch.plan;

import hunternif.voxarch.vector.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The architectural plan composed of {@link Room}s and {@link Gate}s between
 * them.
 * @author Hunternif
 */
public class ArchPlan {

	/** The container for all structures in this plan. */
	private final Room base = new Room(this, null, new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);
	
	private final List<Gate> gates = new ArrayList<Gate>();
	
	public void addGate(Gate gate) {
		gates.add(gate);
	}
	
	public List<Gate> getGates() {
		return gates;
	}
	
	public Room getBase() {
		return base;
	}

}
