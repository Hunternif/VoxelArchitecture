package hunternif.voxarch.mc;

import hunternif.voxarch.builder.BuildContext;
import hunternif.voxarch.builder.MainBuilder;
import hunternif.voxarch.plan.IIncrementalBuilding;
import hunternif.voxarch.plan.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IncrementalBuilder {
	private final IIncrementalBuilding building;
	private final MainBuilder builder;
	private final BuildContext context;
	private final Structure structure;
	
	public IncrementalBuilder(
			IIncrementalBuilding building,
			Structure structure,
			MainBuilder builder,
			BuildContext context
	) {
		this.building = building;
		this.builder = builder;
		this.context = context;
		this.structure = structure;
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event) {
		if (event.world.getGameTime() % 20 == 0) { // once per second
			if (building.isDone()) {
				System.out.println("done building");
				MinecraftForge.EVENT_BUS.unregister(this);
			} else {
				System.out.println("build!");
				building.buildStep();
				builder.build(structure, new MCWorld(event.world), context);
			}
		}
	}
}
