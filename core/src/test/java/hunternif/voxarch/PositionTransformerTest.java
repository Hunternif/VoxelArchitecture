package hunternif.voxarch;

import static hunternif.voxarch.util.Direction.*;
import static org.junit.Assert.*;

import hunternif.voxarch.storage.*;
import hunternif.voxarch.util.Direction;
import hunternif.voxarch.util.PositionTransformer;
import hunternif.voxarch.util.StructureUtil;

import org.junit.Test;

/**
 * @author Hunternif
 */
public class PositionTransformerTest { 
	@Test
	public void testRotate90() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 3, block);
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(3, 1, 2);
		PositionTransformer trans = new PositionTransformer(out).translate(0, 0, 1).rotateY(90);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		for (int x = 0; x < 3; x++) {
			for (int z = 0; z < 2; z++) {
				assertEquals(block, out.getBlock(x, 0, z));
			}
		}
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, trans.getBlock(x, 0, z));
			}
		}
	}
	
	@Test
	public void testRotate180() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 3, block);
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(2, 1, 3);
		PositionTransformer trans = new PositionTransformer(out).translate(1, 0, 2).rotateY(180);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, out.getBlock(x, 0, z));
			}
		}
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, trans.getBlock(x, 0, z));
			}
		}
	}
	
	@Test
	public void testRotate45() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 2, block);
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(3, 1, 3);
		PositionTransformer trans = new PositionTransformer(out).translate(0.5, 0, 1).rotateY(45);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		assertEquals(null, out.getBlock(0, 0, 0));
		assertEquals(null, out.getBlock(1, 0, 1)); // This is an unfortunate hole in the middle.
		assertEquals(null, out.getBlock(2, 0, 2));
		assertEquals(null, out.getBlock(2, 0, 0));
		assertEquals(null, out.getBlock(0, 0, 2));
		assertEquals(block, out.getBlock(1, 0, 0));
		assertEquals(block, out.getBlock(0, 0, 1));
		assertEquals(block, out.getBlock(1, 0, 2));
		assertEquals(block, out.getBlock(2, 0, 1));
	}
	
	@Test
	public void testRotateALittle() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 3, block);
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(2, 1, 3);
		PositionTransformer trans = new PositionTransformer(out).rotateY(3);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, out.getBlock(x, 0, z));
			}
		}
	}
	
	@Test
	public void testGenerateBox() {
		BlockData block = new BlockData("B");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(4, 1, 4);
		PositionTransformer trans = new PositionTransformer(out);
		for (int i = 0; i < 4; i++) {
			trans.setBlock(1, 0, 0, block);
			trans.setBlock(2, 0, 0, block);
			trans.translate(3, 0, 0).rotateY(-90);
		}
		assertEquals(block, out.getBlock(1, 0, 0));
		assertEquals(block, out.getBlock(2, 0, 0));
		assertEquals(block, out.getBlock(3, 0, 1));
		assertEquals(block, out.getBlock(3, 0, 2));
		assertEquals(block, out.getBlock(1, 0, 3));
		assertEquals(block, out.getBlock(2, 0, 3));
		assertEquals(block, out.getBlock(0, 0, 1));
		assertEquals(block, out.getBlock(0, 0, 2));
		
		assertEquals(null, out.getBlock(0, 0, 0));
		assertEquals(null, out.getBlock(3, 0, 0));
		assertEquals(null, out.getBlock(0, 0, 3));
		assertEquals(null, out.getBlock(3, 0, 3));
		assertEquals(null, out.getBlock(1, 0, 1));
		assertEquals(null, out.getBlock(1, 0, 2));
		assertEquals(null, out.getBlock(2, 0, 1));
		assertEquals(null, out.getBlock(2, 0, 2));
	}
	
	@Test
	public void testGenerateUnevenBoxWithStack() {
		BlockData block = new BlockData("B");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(3, 1, 4);
		PositionTransformer trans = new PositionTransformer(out);
		trans.pushTransformation();
		
		trans.setBlock(1, 0, 0, block);
		trans.translate(2, 0, 0).rotateY(-90);
		trans.setBlock(1, 0, 0, block);
		trans.setBlock(2, 0, 0, block);
		
		trans.popTransformation();
		trans.setBlock(0, 0, 1, block);
		trans.setBlock(0, 0, 2, block);
		trans.translate(0, 0, 3).rotateY(90);
		trans.setBlock(0, 0, 1, block);
		
		assertEquals(block, out.getBlock(1, 0, 0));
		assertEquals(block, out.getBlock(2, 0, 1));
		assertEquals(block, out.getBlock(2, 0, 2));
		assertEquals(block, out.getBlock(1, 0, 3));
		assertEquals(block, out.getBlock(0, 0, 1));
		assertEquals(block, out.getBlock(0, 0, 2));
		
		assertEquals(null, out.getBlock(0, 0, 0));
		assertEquals(null, out.getBlock(2, 0, 0));
		assertEquals(null, out.getBlock(0, 0, 3));
		assertEquals(null, out.getBlock(2, 0, 3));
		assertEquals(null, out.getBlock(1, 0, 1));
		assertEquals(null, out.getBlock(1, 0, 2));
	}

	@Test
	public void testRotateBlock() {
		BlockData block = new BlockData("B");
		block.setOrientation(Direction.EAST);
		CachedRotationStorage out = new CachedRotationStorage();
		PositionTransformer trans = new PositionTransformer(out);
		trans.rotateY(90);

		trans.setBlock(0, 0, 0, block);
		assertEquals(NORTH, out.orientation);

		trans.rotateY(90);
		trans.setBlock(0, 0, 0, block);
		assertEquals(WEST, out.orientation);

		trans.rotateY(90);
		trans.setBlock(0, 0, 0, block);
		assertEquals(SOUTH, out.orientation);

		trans.rotateY(90);
		trans.setBlock(0, 0, 0, block);
		assertEquals(EAST, out.orientation);

		trans.rotateY(90);
		trans.setBlock(0, 0, 0, block);
		assertEquals(NORTH, out.orientation);
	}

	@Test
	public void testNestedRotateBlock() {
		BlockData block = new BlockData("B");
		block.setOrientation(Direction.EAST);
		CachedRotationStorage out = new CachedRotationStorage();
		PositionTransformer trans1 = new PositionTransformer(out);
		trans1.rotateY(90);
		PositionTransformer trans2 = new PositionTransformer(trans1);
		trans2.rotateY(90);

		trans2.setBlock(0, 0, 0, block);
		assertEquals(WEST, out.orientation);

		trans2.rotateY(90);
		trans2.setBlock(0, 0, 0, block);
		assertEquals(SOUTH, out.orientation);

		trans2.rotateY(90);
		trans2.setBlock(0, 0, 0, block);
		assertEquals(EAST, out.orientation);

		trans2.rotateY(90);
		trans2.setBlock(0, 0, 0, block);
		assertEquals(NORTH, out.orientation);

		trans2.rotateY(90);
		trans2.setBlock(0, 0, 0, block);
		assertEquals(WEST, out.orientation);
	}

	private static class CachedRotationStorage implements IFixedBlockStorage {
		Direction orientation;
		@Override
		public void setBlock(int x, int y, int z, BlockData block) {
			orientation = block.getOrientation();
		}
		@Override
		public int getWidth() { return 0; }
		@Override
		public int getHeight() { return 0; }
		@Override
		public int getLength() { return 0; }
		@Override
		public BlockData getBlock(int x, int y, int z) { return null; }
		@Override
		public void clearBlock(int x, int y, int z) { }
	}
}
