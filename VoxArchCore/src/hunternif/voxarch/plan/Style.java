package hunternif.voxarch.plan;

import hunternif.voxarch.storage.Structure;

/**
 * Style defines the concrete appearance of elements such as joint arches,
 * columns,decorations etc. All methods may return different variants based on
 * parameters of the node arguments (i.e. dungeon walls for lower floors).
 * Size of all segments is N*{@link #cellSize}.
 */
public interface Style {
	/**
	 * Unit of size, corresponds to corridor width and minimum room size.
	 * Cell size of the Config has to be the same as in this Style in order
	 * for them to be compatible.
	 */
	int cellSize();
	/**
	 * Variants of gate between NodeJoint and Node, facing south.
	 */
	Structure[] getJointGate(NodeJoint joint, Node node);
	/**
	 * Variants of segment of corridor wall, running from west to east, the
	 * south side facing outward.
	 */
	Structure[] getCorridorWallSegment(NodeCorridor corridor);
	/**
	 * Variants of corridor corner, the outward angle facing south-east.
	 */
	Structure[] getCorridorCorner(NodeCorridor corridor);
	/** 
	 * Variants of segment of room wall, running from west to east, the south
	 * side facing outward.
	 */
	Structure[] getRoomWallSegment(NodeRoom room);
	/** 
	 * Variants of floor segment.
	 */
	Structure[] getFloorSegment(Node node);
	/** 
	 * Variants of stair segment, going up from west to east.
	 */
	Structure[] getStairSegment(NodeStairs node);
	/**
	 * Variants of segment of stair corridor wall, running from west to east,
	 * the south side facing outward.
	 */
	Structure[] getStairWallSegment(NodeStairs corridor);
	
	/**
	 * Variants of segment of an edge of the roof, running from west to east,
	 * the south side facing outward.
	 */
	Structure[] getRoofEdgeSegment(Node node);
	/**
	 * Variants of roof corner, the outward angle facing south-east.
	 */
	Structure[] getRoofCorner(Node node);
	/**
	 * Variants of segment of the corridor roof, running from west to east.
	 */
	Structure[] getCorridorRoofSegment(NodeCorridor corridor);
	
	//TODO: bulk roofs for rooms and stairs
	//TODO: bridges and other terrain-related features
	
	/**
	 * Adds content to sufficiently large rooms, i.e. rows of columns, water pools,
	 * tables, thrones etc.
	 */
	//void decorateRoom(Structure room);
	
	/** Random floor-based decorations of various size. */
	Structure[] getDecorations();
}
