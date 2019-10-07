package hunternif.voxarch;

import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.gen.impl.SimpleCeilingGenerator;
import hunternif.voxarch.gen.impl.SimpleFloorGenerator;
import hunternif.voxarch.gen.impl.SimpleHorGateGenerator;
import hunternif.voxarch.gen.impl.SimpleVerGateGenerator;
import hunternif.voxarch.gen.impl.SimpleWallGenerator;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;

import org.junit.Before;

/**
 * 
 * @author Hunternif
 *
 */
public class GeneratorTest {
	protected static BlockData blockFloor = new BlockData(1);
	protected static BlockData blockWall = new BlockData(2);
	protected static BlockData blockCeil = new BlockData(3);
	protected static Materials mat = new Materials() {
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
		public BlockData[] ceilingBlocks() {
			return new BlockData[] {blockCeil};
		}
		@Override
		public BlockData oneBlockProp(String name) {
			return null;
		}
	};
	
	protected Generator gen;
	protected MultiDimIntArrayBlockStorage out;
	
	@Before
	public void setup() {
		out = new MultiDimIntArrayBlockStorage(10, 10, 10);
		gen = new Generator(out);
		gen.setDefaultCeilingGenerator(new SimpleCeilingGenerator());
		gen.setDefaultFloorGenerator(new SimpleFloorGenerator());
		gen.setDefaultWallGenerator(new SimpleWallGenerator());
		gen.setDefaultHorGateGenerator(new SimpleHorGateGenerator());
		gen.setDefaultVerGateGenerator(new SimpleVerGateGenerator());
		gen.setDefaultMaterials(mat);
	}
}
