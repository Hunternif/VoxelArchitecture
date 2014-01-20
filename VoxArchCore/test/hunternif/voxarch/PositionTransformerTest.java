package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;
import hunternif.voxarch.storage.Structure;
import hunternif.voxarch.util.IntVec3;
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
		PositionTransformer trans = new PositionTransformer(out, 0, 0, 2, 90, false);
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
		PositionTransformer trans = new PositionTransformer(out, 2, 0, 3, 180, false);
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
		PositionTransformer trans = new PositionTransformer(out, 0, 0, 1.5, 45, false);
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
	public void testRotate45CloseGaps() {
		BlockData block = new BlockData(1);
		Structure box = StructureUtil.createFilledBox(MultiDimIntArrayBlockStorage.factory, 2, 1, 2, block);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 3);
		PositionTransformer trans = new PositionTransformer(out, 0, 0, 1.5, 45, true);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		assertEquals(null, out.getBlock(0, 0, 0));
		assertEquals(block, out.getBlock(1, 0, 1)); // No hole
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
		PositionTransformer trans = new PositionTransformer(out, 3, false);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, out.getBlock(x, 0, z));
			}
		}
	}
	
	@Test
	public void testRotate45Large() {
		BlockData block = new BlockData(1);
		Structure box = StructureUtil.createFilledBox(MultiDimIntArrayBlockStorage.factory, 50, 1, 50, block);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(71, 1, 71);
		PositionTransformer trans = new PositionTransformer(out, 0, 0, 35, 45, true);
		StructureUtil.pasteStructure(trans, box.getStorage(), 0, 0, 0);
		//System.out.println(((MultiDimIntArrayBlockStorage)out).printLayer(0));
		for (int x = 0; x < 71; x++) {
			for (int z = 0; z < 71; z++) {
				if (x + z >= 35 && x <= 35 + z && z <= 35 + x && x + z < 106) {
					// No holes in the middle:
					assertEquals(block, out.getBlock(x, 0, z));
				}
			}
		}
		assertEquals(block, out.getBlock(35, 0, 0));
		assertEquals(block, out.getBlock(0, 0, 35));
	}
}
