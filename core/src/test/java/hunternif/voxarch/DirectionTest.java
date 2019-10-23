package hunternif.voxarch;

import static org.junit.Assert.*;
import hunternif.voxarch.util.Direction;

import org.junit.Test;

/**
 * @author Hunternif
 */
public class DirectionTest {

	@Test
	public void testClosest() {
		assertEquals(Direction.EAST, Direction.closestTo(-1));
		assertEquals(Direction.EAST, Direction.closestTo(0));
		assertEquals(Direction.EAST, Direction.closestTo(1));
		
		assertEquals(Direction.NORTH, Direction.closestTo(89));
		assertEquals(Direction.NORTH, Direction.closestTo(90));
		assertEquals(Direction.NORTH, Direction.closestTo(91));
		
		assertEquals(Direction.WEST, Direction.closestTo(179));
		assertEquals(Direction.WEST, Direction.closestTo(180));
		assertEquals(Direction.WEST, Direction.closestTo(181));
		
		assertEquals(Direction.SOUTH, Direction.closestTo(269));
		assertEquals(Direction.SOUTH, Direction.closestTo(270));
		assertEquals(Direction.SOUTH, Direction.closestTo(271));
		
		assertEquals(Direction.EAST, Direction.closestTo(361));
		assertEquals(Direction.EAST, Direction.closestTo(-40));
		assertEquals(Direction.EAST, Direction.closestTo(40));
		assertEquals(Direction.NORTH, Direction.closestTo(50));
		assertEquals(Direction.WEST, Direction.closestTo(540));
		assertEquals(Direction.WEST, Direction.closestTo(-170));
		assertEquals(Direction.NORTH, Direction.closestTo(-250));
	}
	
	@Test
	public void testRotate() {
		assertEquals(Direction.NORTH, Direction.NORTH.rotate(44));
		assertEquals(Direction.NORTH, Direction.NORTH.rotate(-44));
		assertEquals(Direction.NORTH, Direction.EAST.rotate(46));
		assertEquals(Direction.EAST, Direction.NORTH.rotate(-46));
		assertEquals(Direction.SOUTH, Direction.EAST.rotate(-46));
		// Edge cases:
		assertEquals(Direction.NORTH, Direction.NORTH.rotate(45));
		assertEquals(Direction.EAST, Direction.EAST.rotate(45));
		assertEquals(Direction.EAST, Direction.EAST.rotate(-45));
	}

	@Test
	public void testRotateBig() {
		assertEquals(Direction.NORTH, Direction.EAST.rotate(90));
		assertEquals(Direction.WEST, Direction.EAST.rotate(180));
		assertEquals(Direction.SOUTH, Direction.EAST.rotate(270));
		assertEquals(Direction.EAST, Direction.EAST.rotate(360));
		assertEquals(Direction.NORTH, Direction.EAST.rotate(450));
		assertEquals(Direction.SOUTH, Direction.EAST.rotate(-90));
		assertEquals(Direction.WEST, Direction.EAST.rotate(-180));
		assertEquals(Direction.NORTH, Direction.EAST.rotate(-270));
		assertEquals(Direction.EAST, Direction.EAST.rotate(-360));
		assertEquals(Direction.SOUTH, Direction.EAST.rotate(-450));
	}
}
