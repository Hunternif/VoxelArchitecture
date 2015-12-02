package hunternif.voxarch.mc;

import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.plan.gate.WallAlignedHorGateFactory;
import hunternif.voxarch.vector.Vec3;

public class RandomPlan {
	private static IGateFactory gateFactory = new WallAlignedHorGateFactory();
	
	public static ArchPlan create() {
		ArchPlan plan = new ArchPlan();
		randomGrid(plan);
		//randomBox(plan);
		return plan;
	}
	
	/** Simple roundish room **/
	public static void oneRoundishRoom(ArchPlan plan) {
		plan.getBase().addChild(new Vec3(0, 0, 0), new Vec3(16, 5, 16), 0).setHasCeiling(false).createRoundWalls(8);
	}
	
	/** A random-sized box with 4 walls. */
	public static void randomBox(ArchPlan plan) {
		//TODO BUG: In odd-sized rooms there are holes in the floor.
		int size = 8;//3 + (int)Math.round(10*Math.random());
		System.out.println("Size: " + size);
		plan.getBase().addChild(new Vec3(0, 0, 0), new Vec3(size, 3, size), 45).setHasCeiling(false).createFourWalls();
	}
	
	/** A flat grid of random-sized interconnected rooms **/
	public static void randomGrid(ArchPlan plan) {
		Vec3 roomSize = new Vec3(4, 5, 4);
		int roomSpacing = 1;
		Vec3 sizeJitter = new Vec3(0, 0, 0);
		int N = 3;
		
		// Step 1. Create a NxN grid of rooms, randomize their size
		Vec3 corner = new Vec3(Math.round(-(roomSize.x + roomSpacing)*(N-1)/2), 0, Math.round(-(roomSize.z + roomSpacing)*(N-1)/2));
		Vec3 curCoords = corner.clone();
		Room[][] roomArray = new Room[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				Vec3 size = roomSize.clone();
				size.x += Math.round(2 * (Math.random() - 0.5) * sizeJitter.x);
				size.y += Math.round(2 * (Math.random() - 0.5) * sizeJitter.y);
				size.z += Math.round(2 * (Math.random() - 0.5) * sizeJitter.z);
				Room room = new Room(curCoords, size, 0).setHasCeiling(false);
				room.createFourWalls();
				roomArray[i][j] = room;
				plan.getBase().addChild(room);
				curCoords.x += roomSize.x + roomSpacing;
			}
			curCoords.x = corner.x;
			curCoords.z += roomSize.z + roomSpacing;
		}
		// Step 2. Interconnect adjacent rooms
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i < N-1) {
					plan.getBase().addGate(gateFactory.create(roomArray[i][j], roomArray[i+1][j]));
				}
				if (j < N-1) {
					plan.getBase().addGate(gateFactory.create(roomArray[i][j], roomArray[i][j+1]));
				}
			}
		}
	}
}
