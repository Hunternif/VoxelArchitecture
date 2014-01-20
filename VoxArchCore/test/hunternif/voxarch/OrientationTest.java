package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.util.BlockOrientation;

import org.junit.Test;

/**
 * @author Hunternif
 */
public class OrientationTest {

	@Test
	public void testClosest() {
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(-1));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(0));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(1));
		
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(89));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(90));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(91));
		
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(179));
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(180));
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(181));
		
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.closestTo(269));
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.closestTo(270));
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.closestTo(271));
		
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(361));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(-40));
		assertEquals(BlockOrientation.EAST, BlockOrientation.closestTo(40));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(50));
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(540));
		assertEquals(BlockOrientation.WEST, BlockOrientation.closestTo(-170));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.closestTo(-250));
	}
	
	@Test
	public void testRotate() {
		assertEquals(BlockOrientation.NORTH, BlockOrientation.NORTH.rotate(44));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.NORTH.rotate(-44));
		assertEquals(BlockOrientation.NORTH, BlockOrientation.EAST.rotate(46));
		assertEquals(BlockOrientation.EAST, BlockOrientation.NORTH.rotate(-46));
		assertEquals(BlockOrientation.SOUTH, BlockOrientation.EAST.rotate(-46));
		// Edge cases:
		assertEquals(BlockOrientation.NORTH, BlockOrientation.NORTH.rotate(45));
		assertEquals(BlockOrientation.EAST, BlockOrientation.EAST.rotate(45));
		assertEquals(BlockOrientation.EAST, BlockOrientation.EAST.rotate(-45));
	}

}
