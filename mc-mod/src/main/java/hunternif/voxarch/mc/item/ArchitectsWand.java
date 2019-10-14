package hunternif.voxarch.mc.item;

import hunternif.voxarch.builder.BuildContext;
import hunternif.voxarch.builder.MainBuilder;
import hunternif.voxarch.mc.MCEnvironment;
import hunternif.voxarch.mc.MCWorld;
import hunternif.voxarch.plan.Structure;
import hunternif.voxarch.sandbox.castle.CastleSetup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static hunternif.voxarch.mc.config.BuilderSetupKt.getDefaultContext;

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
//			Generator gen = new Generator(new MCWorld(world));
//			gen.setDefaultMaterials(new SimpleMaterials());
//			gen.setDefaultWallGenerator(new SimpleTorchlitWallGen());
//			gen.setDefaultWallGenerator(new CrenellationGen(2, 3, 1, 1));
//			gen.setPropGeneratorForName("torch", new OneBlockPropGen("torch"));

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
			BuildContext context = getDefaultContext();
			CastleSetup castleSetup = new CastleSetup(MCEnvironment.environment);
			castleSetup.setup(context);
			Structure tower = castleSetup.squareTower(2, 6, 4, 6);
			tower.getOrigin().set(pos.getX(), pos.getY(), pos.getZ());
			tower.setRotationY(new Random().nextInt(2) * 45);
			new MainBuilder().build(tower, new MCWorld(world), context);

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
