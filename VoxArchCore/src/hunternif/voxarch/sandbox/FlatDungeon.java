package hunternif.voxarch.sandbox;

import hunternif.voxarch.plan.IIncrementalBuilding;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.plan.gate.WallAlignedHorGateFactory;
import hunternif.voxarch.util.IWeightedOption;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.util.RandomUtil;
import hunternif.voxarch.vector.Vec3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Random dungeon consisting of rooms and corridors, each at the same  level.
 * Corridor lengths and room sizes are chosen at random from a predefined set.
 * Each room has a random number of outgoing corridors in random places.
 * Corridors can be extended in any direction via junctions, which are rooms of
 * minimal (corridor width) size.
 * @author Hunternif
 */
public class FlatDungeon extends Room implements IIncrementalBuilding {
	int corridorWidth = 3;
	int corridorHeight = 5;
	int[] corridorLengths = {6, 10, 16};
	int[] roomLengths = {6, 12, 16};
	/** For every given length, the room cannot be lower than the height at corresponding index. */
	int[] roomHeights = {6, 8, 10};
	int maxNewCorridors = 6;
	/** Generation will stop when the total corridor length accumulated from the
	 * starting node reaches this value. Also it can be stopped randomly at any point. */
	int maxTotalLength = 200;
	
	private static enum CorridorOption implements IWeightedOption {
		JUNCTION(0.6), ROOM(0);//0.4);
		
		private CorridorOption(double probability) {
			this.probability = probability;
		}
		final double probability;
		@Override
		public double probability() {
			return probability;
		}
	}
	private static enum JunctionOption implements IWeightedOption {
		DEAD_END(0.3), TURN(0.4), FORK(0.2), CROSSROADS(0.1);
		
		private JunctionOption(double probability) {
			this.probability = probability;
		}
		final double probability;
		@Override
		public double probability() {
			return probability;
		}
	}
	private static enum TurnOption implements IWeightedOption {
		STRAIGHT(0.4), LEFT(0.2), RIGHT(0.2), LEFT_45(0.1), RIGHT_45(0.1);
		
		private TurnOption(double probability) {
			this.probability = probability;
		}
		final double probability;
		@Override
		public double probability() {
			return probability;
		}
	}
	private static enum ForkOption implements IWeightedOption {
		LEFT_STRAIGHT(0.33), RIGHT_STRAIGHT(0.33), TEE(0.34);
		
		private ForkOption(double probability) {
			this.probability = probability;
		}
		final double probability;
		@Override
		public double probability() {
			return probability;
		}
	}
	
	
	/** Each new corridor is appended to this queue to be visited later to continue building.
	 * Rooms are not appended to the queue, but build instantly.*/
	private final Queue<Corridor> corridorQueue = new LinkedList<Corridor>();
	
	private final Random rand = new Random();
	
	private final IGateFactory gateFactory = new WallAlignedHorGateFactory();
	
	public FlatDungeon(Vec3 origin, double rotationY) {
		super(origin, Vec3.ZERO, rotationY);
		// Start with a medium-sized room:
		Corridor start = new Corridor(Vec3.ZERO, corridorLengths[1], 0, 0);
		addChild(start);
		start.createFourWalls();
		corridorQueue.add(start);
		//TODO update size as it builds.
		//TODO fix overlaying.
	}
	
	/** WARNING! This may be very resource intensive and may freeze the game.
	 * Consider periodically calling {@link #buildStep()} instead. */
	public void buildAll() {
		// Whenever a corridor creates a new room, it is immediately built, and
		// new corridors are randomly started from that room.
		while (!corridorQueue.isEmpty()) {
			buildStep();
		}
	}
	
	@Override
	public void buildStep() {
		if (corridorQueue.isEmpty()) return;
		setBuilt(false);
		Corridor node = corridorQueue.poll();
		CorridorOption corridorOption = RandomUtil.randomWeightedOption(CorridorOption.values());
		switch (corridorOption) {
		case JUNCTION:
			// TODO fix gates to not penetrate walls.
			Room junction = new Room(node.endPoint, new Vec3(corridorWidth, corridorHeight, corridorWidth), node.getRotationY());
			junction.createFourWalls();
			addChild(junction);
			addGate(gateFactory.create(node, junction));
			JunctionOption junctionOption = RandomUtil.randomWeightedOption(JunctionOption.values());
			switch (junctionOption) {
			case DEAD_END: break; // Ded.
			case TURN:
				TurnOption turnOption = RandomUtil.randomWeightedOption(TurnOption.values());
				Corridor ext1 = null;
				switch (turnOption) {
				case STRAIGHT:
					ext1 = extend(node, 0);
					break;
				case LEFT:
					ext1 = extend(node, 90);
					break;
				case RIGHT:
					ext1 = extend(node, -90);
					break;
				case LEFT_45:
					ext1 = extend(node, 45);
					break;
				case RIGHT_45:
					ext1 = extend(node, -45);
					break;
				}
				connectNewCorridor(junction, ext1);
				break;
			case FORK:
				ForkOption forkOption = RandomUtil.randomWeightedOption(ForkOption.values());
				ext1 = null; Corridor ext2 = null;
				switch (forkOption) {
				case LEFT_STRAIGHT:
					ext1 = extend(node, 90);
					ext2 = extend(node, 0);
					break;
				case RIGHT_STRAIGHT:
					ext1 = extend(node, 0);
					ext2 = extend(node, -90);
					break;
				case TEE:
					ext1 = extend(node, 90);
					ext2 = extend(node, -90);
					break;
				}
				connectNewCorridor(junction, ext1);
				connectNewCorridor(junction, ext2);
				break;
			case CROSSROADS:
				connectNewCorridor(junction, extend(node, 90));
				connectNewCorridor(junction, extend(node, 0));
				connectNewCorridor(junction, extend(node, -90));
				break;
			}
			break;
		case ROOM:
			// Create a room centered in front of us.
			//TODO make room.
			// Determine how many corridors can fit on each wall, except where we just came from.
			int totalPotentialCorridors = 0;
			for (Wall wall : node.getWalls()) {
				totalPotentialCorridors += MathUtil.roundDown(wall.getLength() / corridorWidth);
			}
			// Pick a random number of corridors (including the current node)
			// and assign each one to a wall at random.
			break;
		}
	}
	
	protected class Corridor extends Room {
		/** Total corridor length accumulated from the starting node: */
		final int totalLength;
		/** Corridor can open into a room or junction at this point. */
		final Vec3 endPoint;
		/**
		 * @param start		point in the middle of the start of the corridor
		 * @param length
		 * @param rotationY
		 * @param totalLength
		 */
		public Corridor(Vec3 origin, int length, double rotationY, int totalLength) {
			super(origin, new Vec3(corridorWidth, corridorHeight, length), rotationY);
			this.totalLength = totalLength;
			endPoint = origin.add(
					length/2*MathUtil.sinDeg(rotationY),
					0,
					length/2*MathUtil.cosDeg(rotationY));
		}
		
	}
	/** Helper method to save on code lines. */
	private Corridor extend(Corridor node, double addedRotationY) {
		int length = corridorLengths[rand.nextInt(corridorLengths.length)];
		double rotationY = node.getRotationY() + addedRotationY;
		Vec3 start = node.endPoint.add(
				length/2*MathUtil.sinDeg(rotationY),
				0,
				length/2*MathUtil.cosDeg(rotationY));
		return new Corridor(start, length, rotationY, node.totalLength + length);
	}
	
	/** Helper method to save on code lines. */
	private void connectNewCorridor(Room junction, Corridor corridor) {
		corridor.createFourWalls();
		addChild(corridor);
		addGate(gateFactory.create(junction, corridor));
		if (corridor.totalLength < maxTotalLength) {
			corridorQueue.add(corridor);
		}
	}

	@Override
	public boolean isDone() {
		return corridorQueue.isEmpty();
	}
}
