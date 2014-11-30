package hunternif.voxarch.mc.item;

import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.gen.impl.SimpleCeilingGenerator;
import hunternif.voxarch.gen.impl.SimpleFloorGenerator;
import hunternif.voxarch.gen.impl.SimpleHorGateGenerator;
import hunternif.voxarch.gen.impl.SimpleWallGenerator;
import hunternif.voxarch.mc.MCWorld;
import hunternif.voxarch.mc.RandomPlan;
import hunternif.voxarch.mc.SimpleMaterials;
import hunternif.voxarch.mc.VoxArchMod;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArchitectsWand extends Item {

	public ArchitectsWand() {
		setMaxStackSize(1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean showAdvanced) {
		list.add("Summon a building");
	}
	
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(VoxArchMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			Generator gen = new Generator(new MCWorld(world));
			gen.setDefaultMaterials(new SimpleMaterials());
			gen.setDefaultCeilingGenerator(new SimpleCeilingGenerator());
			gen.setDefaultFloorGenerator(new SimpleFloorGenerator());
			gen.setDefaultWallGenerator(new SimpleWallGenerator());
			gen.setDefaultHorGateGenerator(new SimpleHorGateGenerator());
			gen.generate(RandomPlan.create(), x, y, z);
		}
		return true;
	}

}
