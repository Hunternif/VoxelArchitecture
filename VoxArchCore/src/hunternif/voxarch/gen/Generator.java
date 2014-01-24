package hunternif.voxarch.gen;

import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.storage.IBlockStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator builds any {@link ArchPlan} into the specified voxel world, using
 * the specified element generators and building materials.
 * @author Hunternif
 */
public class Generator {
	private final IBlockStorage world;
	
	// The materials and generators must be placed in the lists in order of
	// their scope of application, from highest-level rooms (outer buildings)
	// to lowest-level rooms (innermost room subdivisions).
	public final List<Materials> materialStack = new ArrayList<Materials>();
	public final List<CeilingGenerator> ceilingGenStack = new ArrayList<CeilingGenerator>();
	public final List<FloorGenerator> floorGenStack = new ArrayList<FloorGenerator>();
	public final List<HorGateGenerator> horGateGenStack = new ArrayList<HorGateGenerator>();
	public final List<VerGateGenerator> verGateGenStack = new ArrayList<VerGateGenerator>();
	public final List<StairsGenerator> stairsGenStack = new ArrayList<StairsGenerator>();
	public final List<WallGenerator> wallGenStack = new ArrayList<WallGenerator>();
	
	public Generator(IBlockStorage world) {
		this.world = world;
	}

	/** Build the plan with origin at the specified position. */
	public void generate(ArchPlan plan, int x, int y, int z) {
		//TODO: implement generation algorithm.
		/*
		 * Outline of the algorithm:
		 * Recursively build each room starting from the plan base.
		 * 
		 * Building a room:
		 * 1. Clear the volume within the walls of the room. Use RoomBlockOutput
		 * 	for this.
		 * 2. Generate floor, still within the walls.
		 * 3. Generate the walls.
		 * 4. Generate the ceiling, within the walls.
		 * 5*. Push current generators & materials list indices onto stack.
		 * 6*. Advance the index into the generators & materials lists.
		 * 7. Recursively build all child rooms.
		 * 8*. Pop the list indices.
		 * 9. Build the gates within this room. The gate generators will
		 * 	override walls with air to make passages.
		 * 
		 * The steps marked with asterisk (*) are for proposed for a simple
		 * level-based style hierarchy.
		 * TODO: implement a CSS-like style framework.
		 */
	}
}
