package hunternif.voxarch.mc;

import hunternif.voxarch.mc.item.ArchitectsWand;
import hunternif.voxarch.mc.item.ItemRadar;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A demo mod for Voxel Architecture.
 * @author Hunternif
 */
@Mod("voxarch-mc-mod")
public class VoxArchMod {
	
	public static Logger logger = LogManager.getLogger();;
	
	public static ArchitectsWand archWand = new ArchitectsWand(
			new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1)
	);
	public static ItemRadar radar = new ItemRadar(
			new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1)
	);

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
			archWand.setRegistryName(new ResourceLocation("voxarch:architects_wand"));
			radar.setRegistryName(new ResourceLocation("voxarch:radar"));
			itemRegistryEvent.getRegistry().register(archWand);
			itemRegistryEvent.getRegistry().register(radar);
		}
	}
}
