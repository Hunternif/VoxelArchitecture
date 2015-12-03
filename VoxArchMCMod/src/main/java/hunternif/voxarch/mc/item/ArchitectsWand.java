package hunternif.voxarch.mc.item;

import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.gen.impl.OneBlockPropGen;
import hunternif.voxarch.gen.impl.SimpleCeilingGenerator;
import hunternif.voxarch.gen.impl.SimpleFloorGenerator;
import hunternif.voxarch.gen.impl.SimpleHorGateGenerator;
import hunternif.voxarch.mc.IncrementalBuilder;
import hunternif.voxarch.mc.MCWorld;
import hunternif.voxarch.mc.RandomPlan;
import hunternif.voxarch.mc.SimpleMaterials;
import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.sandbox.FlatDungeon;
import hunternif.voxarch.sandbox.SimpleTorchlitWallGen;
import hunternif.voxarch.vector.Vec3;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
			World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			Generator gen = new Generator(new MCWorld(world));
			gen.setDefaultMaterials(new SimpleMaterials());
			//gen.setDefaultCeilingGenerator(new SimpleCeilingGenerator());
			gen.setDefaultFloorGenerator(new SimpleFloorGenerator());
			gen.setDefaultWallGenerator(new SimpleTorchlitWallGen());
			gen.setDefaultHorGateGenerator(new SimpleHorGateGenerator());
			gen.setPropGeneratorForName("torch", new OneBlockPropGen("torch"));
			//gen.generate(RandomPlan.create(), pos.getX(), pos.getY(), pos.getZ());
			ArchPlan plan = new ArchPlan();
			FlatDungeon dungeon = new FlatDungeon(Vec3.ZERO, 0);
			plan.getBase().addChild(dungeon);
			FMLCommonHandler.instance().bus().register(new IncrementalBuilder(dungeon, gen, plan, pos.getX(), pos.getY(), pos.getZ()));
		}
		return true;
	}

}
