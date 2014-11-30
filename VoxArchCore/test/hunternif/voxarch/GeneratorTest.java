package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.gen.impl.*;
import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.AlignedVerGateFactory;
import hunternif.voxarch.plan.gate.WallAlignedHorGateFactory;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;
import hunternif.voxarch.util.DebugUtil;
import hunternif.voxarch.vector.IntVec3;
import hunternif.voxarch.vector.Vec3;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Hunternif
 *
 */
public class GeneratorTest {
	private static BlockData blockFloor = new BlockData(1);
	private static BlockData blockWall = new BlockData(2);
	private static BlockData blockCeil = new BlockData(3);
	private static Materials mat = new Materials() {
		@Override
		public BlockData[] wallBlocks() {
			return new BlockData[] {blockWall};
		}
		@Override
		public BlockData[] stairsBlocks(double slope) {
			return null;
		}
		@Override
		public BlockData[] gateBlocks() {
			return null;
		}
		@Override
		public BlockData[] floorBlocks() {
			return new BlockData[] {blockFloor};
		}
		@Override
		public BlockData[] decorationBlocks() {
			return null;
		}
		@Override
		public BlockData[] ceilingBlocks() {
			return new BlockData[] {blockCeil};
		}
	};
	
	private Generator gen;
	private MultiDimIntArrayBlockStorage out;
	
	@Before
	public void setup() {
		out = new MultiDimIntArrayBlockStorage(10, 6, 10);
		gen = new Generator(out);
		gen.setDefaultCeilingGenerator(new SimpleCeilingGenerator());
		gen.setDefaultFloorGenerator(new SimpleFloorGenerator());
		gen.setDefaultWallGenerator(new SimpleWallGenerator());
		gen.setDefaultHorGateGenerator(new SimpleHorGateGenerator());
		gen.setDefaultVerGateGenerator(new SimpleVerGateGenerator());
		gen.setDefaultMaterials(mat);
	}
	
	@Test
	public void horGate() {
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 5, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(4, 0, 1), new Vec3(3, 6, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new WallAlignedHorGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
		String expected = ""
				+ "2 2 2 2 2 2\n"
				+ "2 1 2 2 1 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 0 0 0 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "2 2 2 2 2 2\n"
				+ "2 3 2 2 0 2\n"
				+ "2 2 2 2 2 2\n"
				+ "\n"
				+ "0 0 0 2 2 2\n"
				+ "0 0 0 2 3 2\n"
				+ "0 0 0 2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(6, 6, 3)));
	}
	
	@Test
	public void verGate() {
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(new Vec3(1, 0, 1), new Vec3(3, 3, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(new Vec3(1, 3, 1), new Vec3(3, 3, 3), 0);
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(new AlignedVerGateFactory().create(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
		String expected = ""
				+ "2 2 2\n"
				+ "2 1 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 0 2\n"
				+ "2 2 2\n"
				+ "\n"
				+ "2 2 2\n"
				+ "2 3 2\n"
				+ "2 2 2";
		
		assertEquals(expected, DebugUtil.printStorageRegion(out, new IntVec3(0, 0, 0), new IntVec3(3, 6, 3)));
	}
}
