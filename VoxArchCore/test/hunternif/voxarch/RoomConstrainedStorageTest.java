package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage;
import hunternif.voxarch.util.DebugUtil;
import hunternif.voxarch.util.RoomConstrainedStorage;
import hunternif.voxarch.util.StructureUtil;
import hunternif.voxarch.vector.Vec3;

import org.junit.Test;

/**
 * 
 * @author Hunternif
 *
 */
public class RoomConstrainedStorageTest {
	@Test
	public void testNoWalls() {
		Room room = new Room(new Vec3(1, 0, 1), new Vec3(3, 1, 3), 0);
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
		Room room = new Room(new Vec3(1, 0, 1), new Vec3(3, 1, 3), 0);
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
		Room room = new Room(new Vec3(1, 0, 1), new Vec3(7, 1, 7), 0);
		room.createRoundWalls(4);
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(7, 1, 7);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		StructureUtil.fill(constrained, block);
		assertEquals("0 0 0 0 0 0 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 0 0 0 0 0 0", DebugUtil.printFixedStorage(out));
	}
	
	@Test
	public void testEightRoundWalls() {
		Room room = new Room(new Vec3(1, 0, 1), new Vec3(7, 1, 7), 0);
		room.createRoundWalls(8);
		BlockData block = new BlockData(1);
		IFixedBlockStorage out = MultiDimIntArrayBlockStorage.factory.createFixed(7, 1, 7);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		StructureUtil.fill(constrained, block);
		assertEquals("0 0 0 0 0 0 0\n" +
					 "0 0 1 1 1 0 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 1 1 1 1 1 0\n" +
					 "0 0 1 1 1 0 0\n" +
					 "0 0 0 0 0 0 0", DebugUtil.printFixedStorage(out));
	}
}
