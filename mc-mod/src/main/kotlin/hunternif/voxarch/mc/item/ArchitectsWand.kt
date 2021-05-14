package hunternif.voxarch.mc.item

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.MainBuilder
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.mc.IncrementalBuilder
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.mc.config.*
import hunternif.voxarch.mc.plan.RandomPlan
import hunternif.voxarch.plan.Prop
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.FlatDungeon
import hunternif.voxarch.vector.Vec3
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import java.util.Random

import hunternif.voxarch.plan.*
import hunternif.voxarch.mc.*
import hunternif.voxarch.mc.config.defaultContext
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.sandbox.castle.builder.SimpleTorchlitWallBuilder
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import kotlin.math.floor

class ArchitectsWand : Item() {

    private val context = defaultContext

    init {
        setMaxStackSize(1)
    }

    override fun addInformation(stack: ItemStack, player: EntityPlayer, list: MutableList<Any?>, showAdvanced: Boolean) {
        list.add("Summon a building")
    }

    override fun onItemRightClick(
        stack: ItemStack, world: World, player: EntityPlayer
    ): ItemStack {
        if (!world.isRemote) {
            val posX = floor(player.posX).toInt()
            val posZ = floor(player.posZ).toInt()
            println("270-yaw: " + (270 - player.rotationYaw))

            val mcWorld = MCWorld(world)

            context.builders.buildersForClass(Wall::class.java).setDefault(
                SimpleTorchlitWallBuilder(MAT_WALL, 4, 3))

            // torch stand for testing
//            val plan = Structure(player.position.toVec3()).apply {
//                prop(Vec3.UNIT_Y, TORCH_STAND)
//                rotationY = 270.0 - player.rotationYaw
//            }
//            MainBuilder().build(plan, mcWorld, context)

            // random corridor
//            var plan = RandomPlan.create()
//            plan.origin = player.position.toVec3()
//            MainBuilder().build(plan, mcWorld, context)

            // simple room
//            val plan = Structure(player.position.toVec3()).apply {
//                room(Vec3(0, 0, 5), Vec3(3, 3, 6)) {
//                    createFourWalls()
//                }
//                rotationY = -player.rotationYaw.toDouble()
//            }
//            MainBuilder().build(plan, mcWorld, context)

            // tower
//            val castleSetup = CastleBlueprint()
//            castleSetup.setup(context)
//            val tower = castleSetup.squareTower(2, 6, 4, 6).apply {
//                origin = pos.toVec3()
//                rotationY = 45.0 * Random().nextInt(2)
//            }
//            MainBuilder().build(tower, mcWorld, context)
//            player.setPositionAndUpdate(pos.x.toDouble(), (pos.y + 10).toDouble(), pos.z.toDouble())

            // castle on mountain
//            val radius = 64
//            val terrain = mcWorld.terrainMap(
//                IntVec2(posX, posZ),
//                IntVec2(radius*2 + 1, radius*2 + 1)
//            )
//            val castle = CastleBlueprint()
//            castle.setup(context)
//            val plan = castle.layout(terrain)
//            MainBuilder().build(plan, mcWorld, context)

            // random flat dungeon
//            val plan = Structure()
//            val dungeon = FlatDungeon(pos.toVec3(), 0.0)
//            context.builders.setDefault(SimpleTorchlitWallBuilder(MAT_WALL, 4, 3))
//            plan.addChild(dungeon)
//            FMLCommonHandler.instance().bus().register(
//                IncrementalBuilder(dungeon, plan, MainBuilder(), context))

            context.builders.setCastleBuilders()

            // animating the building
            val animationWorld = MCWorldAnimation(mcWorld, 200)
            FMLCommonHandler.instance().bus().register(animationWorld)

            // fancy tower
            val pos = Vec3(posX, mcWorld.getTerrainHeight(posX, posZ), posZ)
            val roofShape = RoofShape.values().random()
            val bodyShape = BodyShape.values().random()
//            val plan = tower(
//                pos,
//                size = Vec3(2, 4, 2),
//                roofShape = roofShape,
//                bodyShape = bodyShape
//            )
            val plan = towerWithTurrets(
                origin = pos,
                size = Vec3(18, 32, 18),
                roofShape = roofShape,
                bodyShape = bodyShape,
                placeTurrets = ::place4Turrets
            )
            MainBuilder().build(plan, animationWorld, context)
        }
        return stack
    }

}
