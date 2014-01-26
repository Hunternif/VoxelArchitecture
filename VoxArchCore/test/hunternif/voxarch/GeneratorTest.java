package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.gen.impl.*;
import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;
import hunternif.voxarch.util.RoomUtil;
import hunternif.voxarch.vector.Vec3;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
		gen.setDefaultMaterials(mat);
	}
	
	@Test
	public void test2Rooms() {
		ArchPlan plan = new ArchPlan();
		Room room1 = new Room(plan.getBase(), new Vec3(1, 0, 1), new Vec3(3, 4, 3), 0);
		room1.createFourWalls();
		Room room2 = new Room(plan.getBase(), new Vec3(4, 0, 1), new Vec3(3, 5, 3), 0);
		//FIXME: bug: the 2nd room doesn't have ceiling nor floor
		room2.createFourWalls();
		plan.getBase().addChild(room1);
		plan.getBase().addChild(room2);
		plan.getBase().addGate(RoomUtil.createHorGateBetween(room1, room2));
		gen.generate(plan, 0, 0, 0);
		
		for (int y = 0; y < 6; y++) {
			System.out.println(out.printLayer(y));
			System.out.println("\n");
		}
		fail();
		//assertEquals(blockFloor, out.getBlock(x, 0, z));
	}
}
