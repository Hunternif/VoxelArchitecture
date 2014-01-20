package hunternif.voxarch.plan.style;

/**
 * This style defines the dimensional features of the architectural plan, such
 * as characteristic size, floor height etc.
 * @author Hunternif
 */
public interface Geometry {
	/**
	 * Unit of size, corresponds to corridor width and minimum room size.
	 */
	int cellSize();
	
	/**
	 * Corresponds to the width of the gate structure.
	 */
	//int corridorWidth();
	/**
	 * Corresponds to the length of the corridor wall segment structure. Actual
	 * corridor length will be N * {@link #cellSize}.
	 */
	int minCorridorLength();
	/** Maximum corridor length. Actual length will be N * {@link #cellSize}. */
	int maxCorridorLength();
	
	/**
	 * Corresponds to the length of the room wall segment structure. Actual
	 * minimum room size can't be less than corridor width.
	 */
	//int minRoomSize();
	/**
	 * Maximum room size. Actual size will be N * {@link #cellSize}.
	 */
	int maxRoomSize();
	
	/** If true, all rooms are square. */
	boolean squareRooms();
	/** If true, all buildings will have no roof. */
	boolean noRoof();
	
	//TODO: variable floor height?
	int floorHeight();
	/** Whether all floors are placed at regular vertical intervals. */  
	boolean regularFloors();
	int maxFloors();
	
	/** Slope of stairs in degrees. */
	float stairSlope();
}
