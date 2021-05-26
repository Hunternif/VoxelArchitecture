package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.MultiDimArrayBlockStorage;
import hunternif.voxarch.storage.Structure;
import hunternif.voxarch.util.StructureUtil;
import hunternif.voxarch.vector.IntVec3;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Hunternif
 */
@Deprecated
@Ignore
public class StructureTest { 
	@Test
	public void testRotate90() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 3, block);
		box.setOrigin(1, 0, 1);
		Structure rotated = StructureUtil.rotate(MultiDimArrayBlockStorage.factory, box, 90, false);
		assertEquals(new IntVec3(3, 1, 2), rotated.getSize());
		for (int x = 0; x < 3; x++) {
			for (int z = 0; z < 2; z++) {
				assertEquals(block, rotated.getStorage().getBlock(x, 0, z));
			}
		}
		assertEquals(new IntVec3(1, 0, 0), rotated.getOrigin());
	}
	
	@Test
	public void testRotate180() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 3, block);
		box.setOrigin(0, 0, 1);
		Structure rotated = StructureUtil.rotate(MultiDimArrayBlockStorage.factory, box, 180, false);
		assertEquals(new IntVec3(2, 1, 3), rotated.getSize());
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, rotated.getStorage().getBlock(x, 0, z));
			}
		}
		assertEquals(new IntVec3(1, 0, 1), rotated.getOrigin());
	}
	
	@Test
	public void testRotate45() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 2, block);
		box.setOrigin(0, 0, 1);
		Structure rotated = StructureUtil.rotate(MultiDimArrayBlockStorage.factory, box, 45, false);
		assertEquals(new IntVec3(3, 1, 3), rotated.getSize());
		assertEquals(null, rotated.getStorage().getBlock(0, 0, 0));
		assertEquals(null, rotated.getStorage().getBlock(1, 0, 1)); // This is an unfortunate hole in the middle.
		assertEquals(null, rotated.getStorage().getBlock(2, 0, 2));
		assertEquals(null, rotated.getStorage().getBlock(2, 0, 0));
		assertEquals(null, rotated.getStorage().getBlock(0, 0, 2));
		assertEquals(block, rotated.getStorage().getBlock(1, 0, 0));
		assertEquals(block, rotated.getStorage().getBlock(0, 0, 1));
		assertEquals(block, rotated.getStorage().getBlock(1, 0, 2));
		assertEquals(block, rotated.getStorage().getBlock(2, 0, 1));
		assertEquals(new IntVec3(1, 0, 2), rotated.getOrigin());
	}
	
	@Test
	public void testRotate45CloseGaps() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 2, block);
		box.setOrigin(0, 0, 1);
		Structure rotated = StructureUtil.rotate(MultiDimArrayBlockStorage.factory, box, 45, true);
		assertEquals(new IntVec3(3, 1, 3), rotated.getSize());
		assertEquals(null, rotated.getStorage().getBlock(0, 0, 0));
		assertEquals(block, rotated.getStorage().getBlock(1, 0, 1)); // No holes.
		assertEquals(null, rotated.getStorage().getBlock(2, 0, 2));
		assertEquals(null, rotated.getStorage().getBlock(2, 0, 0));
		assertEquals(null, rotated.getStorage().getBlock(0, 0, 2));
		assertEquals(block, rotated.getStorage().getBlock(1, 0, 0));
		assertEquals(block, rotated.getStorage().getBlock(0, 0, 1));
		assertEquals(block, rotated.getStorage().getBlock(1, 0, 2));
		assertEquals(block, rotated.getStorage().getBlock(2, 0, 1));
		assertEquals(new IntVec3(1, 0, 2), rotated.getOrigin());
	}
	
	@Test
	public void testRotateALittle() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 2, 1, 3, block);
		box.setOrigin(1, 0, 2);
		Structure rotated = StructureUtil.rotate(MultiDimArrayBlockStorage.factory, box, 3, true);
		assertEquals(new IntVec3(2, 1, 3), rotated.getSize());
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, rotated.getStorage().getBlock(x, 0, z));
			}
		}
		assertEquals(new IntVec3(1, 0, 2), rotated.getOrigin());
	}
	
	@Test
	public void testRotate45Large() {
		BlockData block = new BlockData("B");
		Structure box = StructureUtil.createFilledBox(MultiDimArrayBlockStorage.factory, 50, 1, 50, block);
		box.setOrigin(0, 0, 25);
		Structure rotated = StructureUtil.rotate(MultiDimArrayBlockStorage.factory, box, -45, false);
		//System.out.println(((MultiDimArrayBlockStorage)rotated.getStorage()).printLayer(0));
		assertEquals(new IntVec3(71, 1, 71), rotated.getSize());
		for (int x = 0; x < 71; x++) {
			for (int z = 0; z < 71; z++) {
				if (x + z >= 35 && x <= 35 + z && z <= 35 + x && x + z < 106) {
					// No holes in the middle:
					assertEquals(block, rotated.getStorage().getBlock(x, 0, z));
				}
			}
		}
		assertEquals(block, rotated.getStorage().getBlock(35, 0, 0));
		assertEquals(block, rotated.getStorage().getBlock(0, 0, 35));
		assertEquals(new IntVec3(18, 0, 53), rotated.getOrigin());
	}
}
