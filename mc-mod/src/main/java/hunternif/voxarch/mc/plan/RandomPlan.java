package hunternif.voxarch.mc.plan;

import hunternif.voxarch.plan.Corridor;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Structure;
import hunternif.voxarch.plan.gate.IGateFactory;
import hunternif.voxarch.plan.gate.WallAlignedHorGateFactory;
import hunternif.voxarch.vector.Vec2;
import hunternif.voxarch.vector.Vec3;

import java.util.Random;

public class RandomPlan {
	private static IGateFactory gateFactory = new WallAlignedHorGateFactory();
	
	public static Structure create() {
		Structure plan = new Structure();
		//randomGrid(plan);
		//randomBox(plan);
		randomCorridor(plan);
		return plan;
	}
	
	/** Simple roundish room **/
	public static void oneRoundishRoom(Structure plan) {
		Room room = new Room(null, Vec3.ZERO, new Vec3(16, 5, 16), 0);
		room.setHasCeiling(false);
		room.createRoundWalls(8);
		plan.addChild(room);
	}
	
	/** A random-sized box with 4 walls. */
	public static void randomBox(Structure plan) {
		int size = 3 + (int)Math.round(10*Math.random());
		System.out.println("Size: " + size);
		Room room = new Room(null, Vec3.ZERO, new Vec3(size, 3, size), (new Random()).nextInt(2)*45);
		room.setHasCeiling(false);
		room.createFourWalls();
		plan.addChild(room);
	}
	
	/** A flat grid of random-sized interconnected rooms **/
	public static void randomGrid(Structure plan) {
		Vec3 roomSize = new Vec3(6, 5, 6);
		int roomSpacing = 1;
		Vec3 sizeJitter = new Vec3(1, 1, 1);
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
				Room room = new Room(curCoords, size);
                room.setHasCeiling(false);
				room.createFourWalls();
				roomArray[i][j] = room;
				plan.addChild(room);
				curCoords.x += roomSize.x + roomSpacing;
			}
			curCoords.x = corner.x;
			curCoords.z += roomSize.z + roomSpacing;
		}
		// Step 2. Interconnect adjacent rooms
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i < N-1 && (Math.random() > 0.5)) {
					plan.addChild(gateFactory.create(roomArray[i][j], roomArray[i+1][j]));
				}
				if (j < N-1 && (Math.random() > 0.5)) {
					plan.addChild(gateFactory.create(roomArray[i][j], roomArray[i][j+1]));
				}
			}
		}
	}
	
	public static void randomCorridor(Structure plan) {
		Corridor cor = new Corridor(null, Vec3.ZERO, new Vec2(4, 3));
		cor.setHasCeiling(false);
		Random rand = new Random();
		Vec3 lastPoint = new Vec3(Vec3.ZERO);
		for (int i = 0; i < 5; i++) {
			lastPoint.addLocal(rand.nextInt(10), 0, rand.nextInt(10));
			cor.appendPoint(lastPoint);
		}
		cor.build();
		plan.addChild(cor);
	}
}
