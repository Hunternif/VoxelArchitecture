package hunternif.voxarch.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(VoxArchMod.archWand, 0,
				new ModelResourceLocation(VoxArchMod.ID + ":architectsWand", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(VoxArchMod.radar, 0,
				new ModelResourceLocation(VoxArchMod.ID + ":radar", "inventory"));
	}
}
