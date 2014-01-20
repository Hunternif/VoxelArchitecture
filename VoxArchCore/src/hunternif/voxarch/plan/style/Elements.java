package hunternif.voxarch.plan.style;

import hunternif.voxarch.plan.Node;
import hunternif.voxarch.plan.NodeCorridor;
import hunternif.voxarch.plan.NodeJoint;
import hunternif.voxarch.plan.NodeRoom;
import hunternif.voxarch.plan.NodeStairs;
import hunternif.voxarch.storage.Structure;

/**
 * This style defines the concrete appearance of elements such as joint arches,
 * columns,decorations etc. All methods may return different variants based on
 * parameters of the node arguments (i.e. dungeon walls for lower floors).
 * Size of all segments is N*{@link #cellSize}. The block IDs in the structures
 * should be mere placeholders to be replaced with concrete IDs by
 * {@link Materials}.
 * @author Hunternif
 */
public interface Elements {
	/**
	 * Unit of size, corresponds to corridor width and minimum room size. Cell
	 * size of the {@link Geometry} has to be the same as for these
	 * {@link Elements} in order for them to be compatible.
	 */
	int cellSize();
	/**
	 * Variants of gate between {@link NodeJoint} and {@link Node}, facing
	 * south.
	 */
	Structure[] jointGate(NodeJoint joint, Node node);
	/**
	 * Variants of segment of corridor wall, running from west to east, the
	 * south side facing outward.
	 */
	Structure[] corridorWallSegment(NodeCorridor corridor);
	
	/**
	 * Variants of segment of room wall, running from west to east, the south
	 * side facing outward.
	 */
	Structure[] roomWallSegment(NodeRoom room);
	/**
	 * Variants of floor segment.
	 */
	Structure[] floorSegment(Node node);
	/** 
	 * Variants of stair segment, going up from west to east.
	 */
	Structure[] stairSegment(NodeStairs node);
	/**
	 * Variants of segment of stair corridor wall, running from west to east,
	 * the south side facing outward.
	 */
	Structure[] stairWallSegment(NodeStairs corridor);
	
	/**
	 * Variants of segment of an edge of the roof, running from west to east,
	 * the south side facing outward.
	 */
	Structure[] roofEdgeSegment(Node node);
	/**
	 * Variants of segment of the corridor roof, running from west to east.
	 */
	Structure[] corridorRoofSegment(NodeCorridor corridor);
	
	//TODO: bulk roofs for rooms and stairs
	//TODO: bridges and other terrain-related features
	
	/**
	 * Adds content to sufficiently large rooms, i.e. rows of columns, water pools,
	 * tables, thrones etc.
	 */
	//void decorateRoom(Structure room);
	
	/** Random floor-based decorations of various size. */
	Structure[] decorations();
}
