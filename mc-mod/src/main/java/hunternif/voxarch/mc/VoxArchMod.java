package hunternif.voxarch.mc;

import hunternif.voxarch.mc.item.ArchitectsWand;
import hunternif.voxarch.mc.item.ItemRadar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

/**
 * A demo mod for Voxel Architecture.
 * @author Hunternif
 */
@Mod(modid=VoxArchMod.ID, name=VoxArchMod.NAME, version=VoxArchMod.VERSION)
public class VoxArchMod {
	public static final String ID = "voxarch";
	public static final String NAME = "Voxel Architecture";
	public static final String VERSION = "0.1-1.7.10";
	public static final String CHANNEL = ID;
	
	@Instance(ID)
	public static VoxArchMod instance;
	
	public static Logger logger;
	
	public static ArchitectsWand archWand;
	public static ItemRadar radar;

	@SidedProxy(clientSide="hunternif.voxarch.mc.ClientProxy", serverSide="hunternif.voxarch.mc.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		archWand = (ArchitectsWand)new ArchitectsWand().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("architectsWand");
		radar = (ItemRadar)new ItemRadar().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("radar");
		GameRegistry.registerItem(archWand, "architectsWand");
		GameRegistry.registerItem(radar, "radar");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
}
