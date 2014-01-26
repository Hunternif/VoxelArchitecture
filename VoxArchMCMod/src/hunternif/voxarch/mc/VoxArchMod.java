package hunternif.voxarch.mc;

import hunternif.voxarch.mc.item.ArchitectsWand;

import java.util.logging.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * A demo mod for Voxel Architecture.
 * @author Hunternif
 */
@Mod(modid=VoxArchMod.ID, name=VoxArchMod.NAME, version=VoxArchMod.VERSION)
@NetworkMod(clientSideRequired=false, serverSideRequired=true)
public class VoxArchMod {
	public static final String ID = "voxarch";
	public static final String NAME = "Voxel Architecture";
	public static final String VERSION = "@@MOD_VERSION@@";
	public static final String CHANNEL = ID;
	
	@Instance(ID)
	public static VoxArchMod instance;
	
	public static Logger logger;
	
	public static ArchitectsWand archWand;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		int bsid = config.getItem("architectsWand", 26930).getInt();
		config.save();
		archWand = (ArchitectsWand)new ArchitectsWand(bsid).setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("architectsWand");
		LanguageRegistry.addName(archWand, "Architect's Wand");
	}
}
