package hunternif.voxarch.mc;

import hunternif.voxarch.builder.BuildContext;
import hunternif.voxarch.builder.MainBuilder;
import hunternif.voxarch.plan.IIncrementalBuilding;
import hunternif.voxarch.plan.Structure;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

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
	public void onTick(WorldTickEvent event) {
		if (event.world.getWorldTime() % 20 == 0) { // once per second
			if (building.isDone()) {
				System.out.println("done building");
				FMLCommonHandler.instance().bus().unregister(this);
			} else {
				System.out.println("build!");
				building.buildStep();
				builder.build(structure, new MCWorld(event.world), context);
			}
		}
	}
}
