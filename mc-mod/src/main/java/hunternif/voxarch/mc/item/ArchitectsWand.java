package hunternif.voxarch.mc.item;

import hunternif.voxarch.builder.BuildContext;
import hunternif.voxarch.builder.MainBuilder;
import hunternif.voxarch.builder.MaterialConfig;
import hunternif.voxarch.mc.IncrementalBuilder;
import hunternif.voxarch.mc.MCEnvironment;
import hunternif.voxarch.mc.MCExtensionsKt;
import hunternif.voxarch.mc.MCWorld;
import hunternif.voxarch.mc.config.BuilderSetupKt;
import hunternif.voxarch.mc.plan.RandomPlan;
import hunternif.voxarch.plan.Prop;
import hunternif.voxarch.plan.Room;
import hunternif.voxarch.plan.Structure;
import hunternif.voxarch.plan.Wall;
import hunternif.voxarch.sandbox.FlatDungeon;
import hunternif.voxarch.sandbox.castle.CastleSetup;
import hunternif.voxarch.sandbox.castle.SimpleTorchlitWallBuilder;
import hunternif.voxarch.vector.Vec3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.Random;

import static hunternif.voxarch.mc.MCExtensionsKt.*;
import static hunternif.voxarch.mc.config.BuilderSetupKt.getDefaultContext;

public class ArchitectsWand extends Item {

	private BuildContext context = getDefaultContext();

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
			System.out.println("270-yaw: " + (270-player.rotationYaw));

			context.getBuilders().buildersForClass(Wall.class).setDefault(
					new SimpleTorchlitWallBuilder(MaterialConfig.WALL, 4, 3));

			// torch stand for testing
//			Structure plan = new Structure(toVec3(pos));
//			plan.addChild(new Prop(Vec3.UNIT_Y, BuilderSetupKt.TORCH_STAND));
//			plan.setRotationY(270-player.rotationYaw);
//			new MainBuilder().build(plan, new MCWorld(world), context);

			// random corridor
			Structure plan = RandomPlan.create();
			plan.setOrigin(toVec3(pos));
			new MainBuilder().build(plan, new MCWorld(world), context);

			// simple room
//			Structure plan = new Structure();
//			plan.setOrigin(toVec3(pos));
//			Room room = new Room(new Vec3(0, 0, 5), new Vec3(3, 3, 6));
//			plan.setRotationY(-player.rotationYaw);
//			room.createFourWalls();
//			plan.addChild(room);
//			new MainBuilder().build(plan, new MCWorld(world), context);

			// tower
//			CastleSetup castleSetup = new CastleSetup(MCEnvironment.environment);
//			castleSetup.setup(context);
//			Structure tower = castleSetup.squareTower(2, 6, 4, 6);
//			tower.setOrigin(toVec3(pos));
//			tower.setRotationY(new Random().nextInt(2) * 45);
//			new MainBuilder().build(tower, new MCWorld(world), context);
//			player.setPositionAndUpdate(pos.getX(), pos.getY() + 10, pos.getZ());

			// random flat dungeon
//			Structure plan = new Structure();
//			FlatDungeon dungeon = new FlatDungeon(toVec3(pos), 0);
//			context.getBuilders().buildersForClass(Wall.class).setDefault(
//					new SimpleTorchlitWallBuilder(MaterialConfig.WALL, 4, 3));
//			plan.addChild(dungeon);
//			FMLCommonHandler.instance().bus().register(
//					new IncrementalBuilder(dungeon, plan, new MainBuilder(), context));
		}
		return true;
	}

}
