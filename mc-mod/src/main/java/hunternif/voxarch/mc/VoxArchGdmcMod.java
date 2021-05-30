package hunternif.voxarch.mc;

import hunternif.voxarch.mc.gdmc.GdmcBuildCommandKt;
import hunternif.voxarch.mc.item.ArchitectsWand;
import hunternif.voxarch.mc.item.GdmcWand;
import hunternif.voxarch.mc.item.ItemRadar;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * A demo mod for Voxel Architecture.
 * @author Hunternif
 */
@Mod("voxarch-gdmc-mod")
public class VoxArchGdmcMod {
	public static final String NAMESPACE = "voxarch";
	
	public static ArchitectsWand archWand = new ArchitectsWand(
			new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)
	);
	public static GdmcWand gdmcWand = new GdmcWand(
			new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)
	);
	public static ItemRadar radar = new ItemRadar(
			new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)
	);

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeEvents {
		@SubscribeEvent
		public static void onServerStarting(RegisterCommandsEvent event) {
			GdmcBuildCommandKt.register(event.getDispatcher());
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModEvents {
		@SubscribeEvent
		public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
			archWand.setRegistryName(new ResourceLocation(NAMESPACE, "architects_wand"));
			gdmcWand.setRegistryName(new ResourceLocation(NAMESPACE, "gdmc_wand"));
			radar.setRegistryName(new ResourceLocation(NAMESPACE, "radar"));
			itemRegistryEvent.getRegistry().register(archWand);
			itemRegistryEvent.getRegistry().register(gdmcWand);
			itemRegistryEvent.getRegistry().register(radar);
		}
	}
}
