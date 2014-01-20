package hunternif.voxarch.mc;

import java.util.logging.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

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
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}
}
