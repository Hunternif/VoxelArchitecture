package hunternif.voxarch;

import static org.junit.Assert.assertEquals;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IFixedBlockStorage;
import hunternif.voxarch.storage.MultiDimArrayBlockStorage;
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
		Room room = new Room(new Vec3(1324, 2345, 45), new Vec3(2, 0, 2));
		BlockData block = new BlockData("B");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(3, 1, 3);
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
		Room room = new Room(new Vec3(354, 23, 45), new Vec3(2, 0, 2));
		room.createFourWalls();
		BlockData block = new BlockData("B");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(3, 1, 3);
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
		Room room = new Room(new Vec3(243, 56, 12), new Vec3(6, 0, 6));
		room.createRoundWalls(4);
		BlockData block = new BlockData("1");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(7, 1, 7);
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
		Room room = new Room(new Vec3(325, 456, 13), new Vec3(6, 0, 6));
		room.createRoundWalls(8);
		BlockData block = new BlockData("1");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(7, 1, 7);
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
	
	@Test
	public void testOffset4Walls() {
		Room room = new Room(new Vec3(54, 24, 685), new Vec3(6, 0, 6));
		room.createFourWalls();
		BlockData block = new BlockData("1");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(7, 1, 7);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		constrained.setOffset(1);
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
	public void testOffset4Walls2() {
		Room room = new Room(new Vec3(1324, 46, 546), new Vec3(6, 0, 6));
		room.createFourWalls();
		BlockData block = new BlockData("1");
		IFixedBlockStorage out = MultiDimArrayBlockStorage.factory.createFixed(7, 1, 7);
		RoomConstrainedStorage constrained = new RoomConstrainedStorage(out, room);
		constrained.setOffset(2);
		StructureUtil.fill(constrained, block);
		assertEquals("0 0 0 0 0 0 0\n" +
					 "0 0 0 0 0 0 0\n" +
					 "0 0 1 1 1 0 0\n" +
					 "0 0 1 1 1 0 0\n" +
					 "0 0 1 1 1 0 0\n" +
					 "0 0 0 0 0 0 0\n" +
					 "0 0 0 0 0 0 0", DebugUtil.printFixedStorage(out));
	}
}
