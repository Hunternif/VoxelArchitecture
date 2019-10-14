package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;
import hunternif.voxarch.storage.Structure;
import hunternif.voxarch.util.PositionTransformer;
import hunternif.voxarch.util.StructureUtil;

import org.junit.Test;

/**
 * @author Hunternif
 */
public class PositionTransformerTest { 
	@Test
	public void testRotate90() {
		BlockData block = new BlockData(1);
		Structure box = StructureUtil.createFilledBox(MultiDimIntArrayBlockStorage.factory, 2, 1, 3, block);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 2);
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
		BlockData block = new BlockData(1);
		Structure box = StructureUtil.createFilledBox(MultiDimIntArrayBlockStorage.factory, 2, 1, 3, block);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(2, 1, 3);
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
		BlockData block = new BlockData(1);
		Structure box = StructureUtil.createFilledBox(MultiDimIntArrayBlockStorage.factory, 2, 1, 2, block);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 3);
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
		BlockData block = new BlockData(1);
		Structure box = StructureUtil.createFilledBox(MultiDimIntArrayBlockStorage.factory, 2, 1, 3, block);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(2, 1, 3);
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
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(4, 1, 4);
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
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 4);
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
}
