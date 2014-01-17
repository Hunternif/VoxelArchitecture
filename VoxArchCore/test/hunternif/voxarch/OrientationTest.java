package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.util.BlockOrientation;

import org.junit.Test;

public class OrientationTest {

	@Test
	public void test() {
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(-1));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(0));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(1));
		
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(89));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(90));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(91));
		
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.closestTo(179));
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.closestTo(180));
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.closestTo(181));
		
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(269));
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(270));
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(271));
		
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(361));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(-40));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(40));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(50));
	}

}
