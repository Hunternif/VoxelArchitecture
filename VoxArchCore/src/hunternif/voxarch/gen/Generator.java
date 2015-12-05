package hunternif.voxarch.gen;

import hunternif.voxarch.plan.*;
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
	private ElementGenerator.Ceiling defaultCeilingGenerator;
	private ElementGenerator.Floor defaultFloorGenerator;
	private ElementGenerator.HorGate defaultHorGateGenerator;
	private ElementGenerator.VerGate defaultVerGateGenerator;
	//private ElementGenerator.Stairs defaultStairsGenerator;
	private ElementGenerator.Wall defaultWallGenerator;
	private final Map<String, Materials> materialsMap = new HashMap<String, Materials>();
	private final Map<String, ElementGenerator.Ceiling> ceilingGenMap = new HashMap<String, ElementGenerator.Ceiling>();
	private final Map<String, ElementGenerator.Floor> floorGenMap = new HashMap<String, ElementGenerator.Floor>();
	private final Map<String, ElementGenerator.HorGate> horGateGenMap = new HashMap<String, ElementGenerator.HorGate>();
	private final Map<String, ElementGenerator.VerGate> verGateGenMap = new HashMap<String, ElementGenerator.VerGate>();
	//private final Map<String, ElementGenerator.Stairs> stairsGenMap = new HashMap<String, ElementGenerator.Stairs>();
	private final Map<String, ElementGenerator.Wall> wallGenMap = new HashMap<String, ElementGenerator.Wall>();
	/** Mapped to prop name, not type! TODO: organize prop names vs types. */
	private final Map<String, ElementGenerator.Prop> propGenMap = new HashMap<String, ElementGenerator.Prop>();

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
		 * 3. Generate the ceiling, again within the walls.
		 * 4. Generate the walls.
		 * 5. Recursively build all child rooms.
		 * 6. Build the gates within this room. The gate generators will
		 * 	override walls with air to make passages.
		 * 
		 * Walls are generated after the ceiling in order that when building
		 * multistorey houses the walls aren't interleaved with stripes of
		 * ceiling blocks.
		 */
		PositionTransformer pos = new PositionTransformer(world);
		pos.translate(x, y, z).rotateY(plan.getBase().getRotationY());
		generateRoom(pos, plan.getBase());
	}
	
	/** Recursively generate the room. The {@link PositionTransformer} should
	 * contain all the transformations made to this room and its parents, so
	 * that its origin is now at the room's origin. */
	protected void generateRoom(PositionTransformer pos, Room room) {
		//TODO: If the room is a flight of stairs, use a sloped position transformer.
		RoomConstrainedStorage volume = new RoomConstrainedStorage(pos, room);
		// Clear volume within the room:
		pos.pushTransformation();
		pos.setCloseGaps(true);
		// Offset ensures that no unnecessary blocks are removed outside the
		// walls. However, this means that in case of non-axis-aligned walls
		// some terrain blocks may be left inside due to roundoff errors.
		// Personally, I prioritize external appearance, so this is acceptable.
		volume.setOffset(0.1);
		// TODO: Reorder the RoomConstrainedStorage to be applied after closing gaps.
		// The holes in the ground are probably caused by the fact that
		// RoomConstrainedStorage cuts off possible block positions too early,
		// and they can't be filled with the closeGaps options.
		pos.translate(-room.getSize().x/2, 0, -room.getSize().z/2);
		StructureUtil.clearStorage(volume);
		pos.popTransformation();
		// If found materials, proceed with generation:
		Materials materials = materialsMap.get(room.getType());
		if (materials == null) materials = defaultMaterials;
		if (materials == null) return; // No materials provided!
		// Generate floor:
		if (room.hasFloor()) {
			ElementGenerator.Floor floorGen = floorGenMap.get(room.getType());
			if (floorGen == null) floorGen = defaultFloorGenerator;
			if (floorGen != null) {
				volume.setOffset(0.1);
				pos.pushTransformation();
				pos.setCloseGaps(true);
				pos.translate(-room.getSize().x/2, 0, -room.getSize().z/2);
				floorGen.generateFloor(volume, new Vec2(room.getSize().x, room.getSize().z), materials);
				pos.popTransformation();
			}
		}
		// Generate ceiling:
		if (room.hasCeiling()) {
			ElementGenerator.Ceiling ceilGen = ceilingGenMap.get(room.getType());
			if (ceilGen == null) ceilGen = defaultCeilingGenerator;
			if (ceilGen != null) {
				volume.setOffset(0.1);
				pos.pushTransformation();
				pos.setCloseGaps(true);
				pos.translate(-room.getSize().x/2, room.getSize().y, -room.getSize().z/2);
				ceilGen.generateCeiling(volume, new Vec2(room.getSize().x, room.getSize().z), materials);
				pos.popTransformation();
			}
		}
		// Generate walls:
		ElementGenerator.Wall wallGen = wallGenMap.get(room.getType());
		if (wallGen == null) wallGen = defaultWallGenerator;
		if (wallGen != null) {
			for (Wall wall : room.getWalls()) {
				if (wall.isTransparent()) continue;
				pos.pushTransformation();
				pos.translate(wall.getP1().x, 0, wall.getP1().y);
				pos.setCloseGaps(false);
				pos.rotateY(wall.getAngleDeg());
				wallGen.generateWall(pos, wall, materials);
				pos.popTransformation();
			}
		}
		// Recursively build all child rooms:
		for (Room child : room.getChildren()) {
			if (child.isBuilt()) continue;
			pos.pushTransformation();
			pos.translate(child.getOrigin()).rotateY(child.getRotationY());
			generateRoom(pos, child);
			pos.popTransformation();
		}
		// Build the gates within this room:
		for (Gate gate : room.getGates()) {
			if (gate.isBuilt()) continue;
			ElementGenerator.Gate gen = null;
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
			pos.pushTransformation();
			pos.translate(gate.getOrigin()).rotateY(gate.getRotationY());
			// Move the origin to the bottom left corner of the gate:
			if (gate.isHorizontal()) {
				pos.translate(-gate.getSize().x/2, 0, 0);
			} else {
				pos.translate(-gate.getSize().x/2, 0, -gate.getSize().y/2);
			}
			pos.setCloseGaps(false);
			gen.generateGate(pos, gate, materials);
			pos.popTransformation();
			gate.setBuilt(true);
		}
		// Build props:
		for (Prop prop : room.getProps()) {
			ElementGenerator.Prop gen = propGenMap.get(prop.getName());
			if (gen != null) {
				Materials propMaterials = materialsMap.get(prop.getType());
				if (propMaterials == null) propMaterials = defaultMaterials;
				pos.pushTransformation();
				pos.translate(prop.getOrigin()).rotateY(prop.getRotationY());
				gen.generateProp(pos, prop, propMaterials);
				pos.popTransformation();
			}
		}
		room.setBuilt(true);
	}

	public void setDefaultMaterials(Materials defaultMaterials) {
		this.defaultMaterials = defaultMaterials;
	}

	public void setDefaultCeilingGenerator(ElementGenerator.Ceiling defaultCeilingGenerator) {
		this.defaultCeilingGenerator = defaultCeilingGenerator;
	}

	public void setDefaultFloorGenerator(ElementGenerator.Floor defaultFloorGenerator) {
		this.defaultFloorGenerator = defaultFloorGenerator;
	}

	public void setDefaultHorGateGenerator(ElementGenerator.HorGate defaultHorGateGenerator) {
		this.defaultHorGateGenerator = defaultHorGateGenerator;
	}

	public void setDefaultVerGateGenerator(ElementGenerator.VerGate defaultVerGateGenerator) {
		this.defaultVerGateGenerator = defaultVerGateGenerator;
	}

	/*public void setDefaultStairsGenerator(ElementGenerator.Stairs defaultStairsGenerator) {
		this.defaultStairsGenerator = defaultStairsGenerator;
	}*/

	public void setDefaultWallGenerator(ElementGenerator.Wall defaultWallGenerator) {
		this.defaultWallGenerator = defaultWallGenerator;
	}
	
	public void setMaterialsForType(String type, Materials materials) {
		materialsMap.put(type, materials);
	}

	public void setCeilingGeneratorForType(String type, ElementGenerator.Ceiling ceilingGenerator) {
		ceilingGenMap.put(type, ceilingGenerator);
	}

	public void setFloorGeneratorForType(String type, ElementGenerator.Floor floorGenerator) {
		floorGenMap.put(type, floorGenerator);
	}

	public void setHorGateGeneratorForType(String type, ElementGenerator.HorGate horGateGenerator) {
		horGateGenMap.put(type, horGateGenerator);
	}

	public void setVerGateGeneratorForType(String type, ElementGenerator.VerGate verGateGenerator) {
		verGateGenMap.put(type, verGateGenerator);
	}

	/*public void setStairsGeneratorForType(String type, ElementGenerator.Stairs stairsGenerator) {
		stairsGenMap.put(type, stairsGenerator);
	}*/

	public void setWallGeneratorForType(String type, ElementGenerator.Wall wallGenerator) {
		wallGenMap.put(type, wallGenerator);
	}
	
	public void setPropGeneratorForName(String name, ElementGenerator.Prop propGenerator) {
		propGenMap.put(name, propGenerator);
	}
}
