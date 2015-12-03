package hunternif.voxarch.mc;

import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.plan.IIncrementalBuilding;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class IncrementalBuilder {
	private final IIncrementalBuilding building;
	private final Generator generator;
	private final ArchPlan plan;
	private final int x, y, z;
	
	public IncrementalBuilder(IIncrementalBuilding building, Generator generator, ArchPlan plan, int x, int y, int z) {
		this.building = building;
		this.generator = generator;
		this.plan = plan;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@SubscribeEvent
	public void onTick(WorldTickEvent event) {
		if (event.world.getWorldTime() % 20 == 0) { // once per second
			if (building.isDone()) {
				System.out.println("done building");
				FMLCommonHandler.instance().bus().unregister(this);
			} else {
				System.out.println("build!");
				building.buildStep();
				generator.generate(plan, x, y, z);
			}
		}
	}
}
