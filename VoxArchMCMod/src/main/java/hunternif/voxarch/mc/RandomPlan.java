package hunternif.voxarch.mc;

import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.vector.Vec3;

public class RandomPlan {
	public static ArchPlan create() {
		ArchPlan plan = new ArchPlan();
		plan.getBase().addChild(new Vec3(0, 0, 0), new Vec3(16, 6, 16), 0).setHasCeiling(false).createRoundWalls(8);
		return plan;
	}
}
