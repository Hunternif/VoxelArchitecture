package hunternif.voxarch.mc.item;

import hunternif.voxarch.gen.Generator;
import hunternif.voxarch.gen.impl.OneBlockPropGen;
import hunternif.voxarch.mc.ExtBlockDataMC;
import hunternif.voxarch.mc.MCWorld;
import hunternif.voxarch.mc.config.MCEnvironment;
import hunternif.voxarch.mc.config.SimpleMaterials;
import hunternif.voxarch.plan.ArchPlan;
import hunternif.voxarch.sandbox.castle.CastleSetup;
import hunternif.voxarch.sandbox.castle.CrenellationGen;

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
//			gen.setDefaultWallGenerator(new SimpleTorchlitWallGen());
			gen.setDefaultWallGenerator(new CrenellationGen(2, 3, 1, 1));
			gen.setPropGeneratorForName("torch", new OneBlockPropGen("torch"));

			// random corridor
//			gen.generate(RandomPlan.create(), pos.getX(), pos.getY(), pos.getZ());

			// simple room
//			ArchPlan plan = new ArchPlan();
//			Room room = new Room(Vec3.ZERO, new Vec3(10, 3, 9));
//			room.createFourWalls();
//			room.setHasCeiling(false);
//			plan.getBase().addChild(room);
//			gen.generate(plan, pos.getX(), pos.getY(), pos.getZ());

			// tower
			CastleSetup castleSetup = new CastleSetup(MCEnvironment.environment);
			castleSetup.setup(gen);
			ArchPlan plan = castleSetup.squareTower(3, 7, 5, 6);
			gen.generate(plan, pos.getX(), pos.getY(), pos.getZ());
			player.setPositionAndUpdate(pos.getX(), pos.getY() + 10, pos.getZ());

			// random flat dungeon
//			ArchPlan plan = new ArchPlan();
//			FlatDungeon dungeon = new FlatDungeon(Vec3.ZERO, 0);
//			plan.getBase().addChild(dungeon);
//			FMLCommonHandler.instance().bus().register(new IncrementalBuilder(dungeon, gen, plan, pos.getX(), pos.getY(), pos.getZ()));
		}
		return true;
	}

}
