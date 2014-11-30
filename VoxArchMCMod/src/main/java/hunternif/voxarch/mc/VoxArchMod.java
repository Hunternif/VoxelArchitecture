package hunternif.voxarch.mc;

import hunternif.voxarch.mc.item.ArchitectsWand;
import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

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
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		archWand = (ArchitectsWand)new ArchitectsWand().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("architectsWand");
		GameRegistry.registerItem(archWand, "architectsWand");
	}
}
