package hunternif.voxarch;

import hunternif.voxarch.plan.Room;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;
import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.util.StructureUtil;
import hunternif.voxarch.vector.Vec3;

import static org.junit.Assert.*;
import org.junit.Test;

public class RoomConstrainedStorageTest {
	@Test
	public void testNoWalls() {
		Room room = new Room(null, null, new Vec3(1.5, 0, 1.5), new Vec3(3, 1, 3), 0);
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 3);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		StructureUtil.fill(constrained, block);
		for (int x = 0; x < 3; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, out.getBlock(x, 0, z));
			}
		}
	}
	
	@Test
	public void testFourWalls() {
		Room room = new Room(null, null, new Vec3(1.5, 0, 1.5), new Vec3(3, 1, 3), 0);
		room.createFourWalls();
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 3);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		StructureUtil.fill(constrained, block);
		for (int x = 0; x < 3; x++) {
			for (int z = 0; z < 3; z++) {
				assertEquals(block, out.getBlock(x, 0, z));
			}
		}
	}
	
	@Test
	public void testFourRoundWalls() {
		Room room = new Room(null, null, new Vec3(1.5, 0, 1.5), new Vec3(3, 1, 3), 0);
		room.createRoundWalls(4);
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(3, 1, 3);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		StructureUtil.fill(constrained, block);
		assertEquals(null, out.getBlock(0, 0, 0));
		assertEquals(block, out.getBlock(1, 0, 0));
		assertEquals(null, out.getBlock(2, 0, 0));
		assertEquals(block, out.getBlock(0, 0, 1));
		assertEquals(block, out.getBlock(1, 0, 1));
		assertEquals(block, out.getBlock(2, 0, 1));
		assertEquals(null, out.getBlock(0, 0, 2));
		assertEquals(block, out.getBlock(1, 0, 2));
		assertEquals(null, out.getBlock(2, 0, 2));
	}
}
