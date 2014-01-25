package hunternif.voxarch.gen;

import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Gate;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.storage.IBlockStorage;
import hunternif.voxarch.util.PositionTransformer;
import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.util.StructureUtil;
import hunternif.voxarch.vector.Vec2;

import java.util.HashMap;
import java.util.Map;

/**
 * Generator builds any {@link ArchPlan} into the specified voxel world, using
 * the specified element generators and building materials.
 * <p>
 * Before calling {@link #generate} set up materials and generators for
 * particular types of rooms and gates. (Example types: "corridor", "kitchen",
 * "dungeon" etc.) Also set up default materials and generators to be used for
 * rooms and gates with no types specified.
 * </p>
 * 
 * @author Hunternif
 */
public class Generator {
	private final IBlockStorage world;

	private Materials defaultMaterials;
	private CeilingGenerator defaultCeilingGenerator;
	private FloorGenerator defaultFloorGenerator;
	private HorGateGenerator defaultHorGateGenerator;
	private VerGateGenerator defaultVerGateGenerator;
	private StairsGenerator defaultStairsGenerator;
	private WallGenerator defaultWallGenerator;
	private final Map<String, Materials> materialsMap = new HashMap<String, Materials>();
	private final Map<String, CeilingGenerator> ceilingGenMap = new HashMap<String, CeilingGenerator>();
	private final Map<String, FloorGenerator> floorGenMap = new HashMap<String, FloorGenerator>();
	private final Map<String, HorGateGenerator> horGateGenMap = new HashMap<String, HorGateGenerator>();
	private final Map<String, VerGateGenerator> verGateGenMap = new HashMap<String, VerGateGenerator>();
	private final Map<String, StairsGenerator> stairsGenMap = new HashMap<String, StairsGenerator>();
	private final Map<String, WallGenerator> wallGenMap = new HashMap<String, WallGenerator>();

	public Generator(IBlockStorage world) {
		this.world = world;
	}

	/** Build the plan with origin at the specified position. */
	public void generate(ArchPlan plan, int x, int y, int z) {
		/*
		 * Outline of the algorithm:
		 * Recursively build each room starting from the plan base.
		 * 
		 * Building a room:
		 * 1. Clear the volume within the walls of the room.
		 * 2. Generate floor, still within the walls.
		 * 3. Generate the walls.
		 * 4. Generate the ceiling, again within the walls.
		 * 5. Recursively build all child rooms.
		 * 6. Build the gates within this room. The gate generators will
		 * 	override walls with air to make passages.
		 */
		PositionTransformer pos = new PositionTransformer(world);
		pos.translate(x, y, z).rotateY(plan.getBase().getRotationY());
		generateRoom(pos, plan.getBase());
	}
	
	/** Recursively generate the room. The {@link PositionTransformer} should
	 * contain all the transformations made to this room and its parents, so
	 * that its origin is now at the room's origin. */
	protected void generateRoom(PositionTransformer pos, Room room) {
		RoomConstrainedStorage volume = new RoomConstrainedStorage(pos, room);
		// Clear volume within the room:
		StructureUtil.clearStorage(volume);
		// If found materials, proceed with generation:
		Materials materials = materialsMap.get(room.getType());
		if (materials == null) materials = defaultMaterials;
		if (materials != null) {
			// Generate floor:
			if (room.hasFloor()) {
				FloorGenerator floorGen = floorGenMap.get(room.getType());
				if (floorGen == null) floorGen = defaultFloorGenerator;
				if (floorGen != null) {
					floorGen.generateFloor(volume, new Vec2(room.getSize().x, room.getSize().z), materials);
				}
			}
			// Generate walls:
			WallGenerator wallGen = wallGenMap.get(room.getType());
			if (wallGen == null) wallGen = defaultWallGenerator;
			if (wallGen != null) {
				for (Wall wall : room.getWalls()) {
					pos.pushTransformation();
					pos.translate(wall.getP1().x, 0, wall.getP1().y);
					pos.rotateY(wall.getAngleDeg());
					wallGen.generateWall(pos, wall, materials);
					pos.popTransformation();
				}
			}
			// Generate ceiling:
			if (room.hasCeiling()) {
				CeilingGenerator ceilGen = ceilingGenMap.get(room.getType());
				if (ceilGen == null) ceilGen = defaultCeilingGenerator;
				if (ceilGen != null) {
					pos.translate(0, room.getSize().y, 0);
					ceilGen.generateCeiling(volume, new Vec2(room.getSize().x, room.getSize().z), materials);
					pos.translate(0, -room.getSize().y, 0);
				}
			}
		}
		// Recursively build all child rooms:
		for (Room child : room.getChildren()) {
			pos.pushTransformation();
			pos.translate(child.getOrigin()).rotateY(child.getRotationY());
			generateRoom(pos, child);
			pos.popTransformation();
		}
		// Build the gates within this room:
		for (Gate gate : room.getGates()) {
			GateGenerator gen = null;
			if (gate.isHorizontal()) {
				gen = horGateGenMap.get(gate.getType());
				if (gen == null) gen = defaultHorGateGenerator;
			} else {
				gen = verGateGenMap.get(gate.getType());
				if (gen == null) gen = defaultVerGateGenerator;
			}
			if (gen == null) continue;
			Materials gateMaterials = materialsMap.get(gate.getType());
			if (gateMaterials == null) gateMaterials = defaultMaterials;
			if (gateMaterials == null) continue;
			pos.pushTransformation();
			pos.translate(gate.getOrigin()).rotateY(gate.getRotationY());
			gen.generateGate(pos, gate, materials);
			pos.popTransformation();
		}
	}

	public void setDefaultMaterials(Materials defaultMaterials) {
		this.defaultMaterials = defaultMaterials;
	}

	public void setDefaultCeilingGenerator(CeilingGenerator defaultCeilingGenerator) {
		this.defaultCeilingGenerator = defaultCeilingGenerator;
	}

	public void setDefaultFloorGenerator(FloorGenerator defaultFloorGenerator) {
		this.defaultFloorGenerator = defaultFloorGenerator;
	}

	public void setDefaultHorGateGenerator(HorGateGenerator defaultHorGateGenerator) {
		this.defaultHorGateGenerator = defaultHorGateGenerator;
	}

	public void setDefaultVerGateGenerator(VerGateGenerator defaultVerGateGenerator) {
		this.defaultVerGateGenerator = defaultVerGateGenerator;
	}

	public void setDefaultStairsGenerator(StairsGenerator defaultStairsGenerator) {
		this.defaultStairsGenerator = defaultStairsGenerator;
	}

	public void setDefaultWallGenerator(WallGenerator defaultWallGenerator) {
		this.defaultWallGenerator = defaultWallGenerator;
	}
	
	public void setMaterialsForType(String type, Materials materials) {
		materialsMap.put(type, materials);
	}

	public void setCeilingGeneratorForType(String type, CeilingGenerator ceilingGenerator) {
		ceilingGenMap.put(type, ceilingGenerator);
	}

	public void setFloorGeneratorForType(String type, FloorGenerator floorGenerator) {
		floorGenMap.put(type, floorGenerator);
	}

	public void setHorGateGeneratorForType(String type, HorGateGenerator horGateGenerator) {
		horGateGenMap.put(type, horGateGenerator);
	}

	public void setVerGateGeneratorForType(String type, VerGateGenerator verGateGenerator) {
		verGateGenMap.put(type, verGateGenerator);
	}

	public void setStairsGeneratorForType(String type, StairsGenerator stairsGenerator) {
		stairsGenMap.put(type, stairsGenerator);
	}

	public void setWallGeneratorForType(String type, WallGenerator wallGenerator) {
		wallGenMap.put(type, wallGenerator);
	}
}
