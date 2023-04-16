package hunternif.voxarch.mc.item

import hunternif.voxarch.builder.MAT_WALL
import hunternif.voxarch.builder.setCastleBuilders
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.vector.Vec3
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

import hunternif.voxarch.mc.*
import hunternif.voxarch.mc.config.defaultContext
import hunternif.voxarch.sandbox.castle.builder.SimpleTorchlitWallBuilder
import hunternif.voxarch.sandbox.castle.turret.*
import hunternif.voxarch.wfc.tiled.WfcTiledModel
import hunternif.voxarch.wfc.WfcColor
import hunternif.voxarch.wfc.tiled.setAirAndGroundBoundary
import hunternif.voxarch.wfc.tiled.wang7x3x7.*
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import kotlin.math.floor

class ArchitectsWand(properties: Properties) : Item(properties) {

    override fun appendHoverText(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<ITextComponent>,
        flag: ITooltipFlag
    ) {
        tooltip.add(StringTextComponent("Summon a building"))
    }

    override fun use(
        world: World, player: PlayerEntity, hand: Hand
    ): ActionResult<ItemStack> {
        if (!world.isClientSide) {
            val posX = floor(player.x).toInt()
            val posZ = floor(player.z).toInt()
            println("270-yaw: " + (270 - player.yRot))

            val context = defaultContext
            val mcWorld = MCWorld(world)

            context.builders.buildersForClass(Wall::class.java).setDefault(
                SimpleTorchlitWallBuilder(MAT_WALL, 4, 3))

            // torch stand for testing
//            val plan = Structure(player.position.toVec3()).apply {
//                prop(Vec3.UNIT_Y, TORCH_STAND)
//                rotationY = 270.0 - player.rotationYaw
//            }
//            RootBuilder().build(plan, mcWorld, context)

            // random corridor
//            var plan = RandomPlan.create()
//            plan.origin = player.position.toVec3()
//            RootBuilder().build(plan, mcWorld, context)

            // simple room
//            val plan = Structure(player.position.toVec3()).apply {
//                room(Vec3(0, 0, 5), Vec3(3, 3, 6)) {
//                    createFourWalls()
//                }
//                rotationY = -player.rotationYaw.toDouble()
//            }
//            RootBuilder().build(plan, mcWorld, context)

            // tower
//            val castleSetup = CastleBlueprint()
//            castleSetup.setup(context)
//            val tower = castleSetup.squareTower(2, 6, 4, 6).apply {
//                origin = pos.toVec3()
//                rotationY = 45.0 * Random().nextInt(2)
//            }
//            RootBuilder().build(tower, mcWorld, context)
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
//            RootBuilder().build(plan, mcWorld, context)

            // random flat dungeon
//            val plan = Structure()
//            val dungeon = FlatDungeon(pos.toVec3(), 0.0)
//            context.builders.setDefault(SimpleTorchlitWallBuilder(MAT_WALL, 4, 3))
//            plan.addChild(dungeon)
//            FMLCommonHandler.instance().bus().register(
//                IncrementalBuilder(dungeon, plan, RootBuilder(), context))

            context.builders.setCastleBuilders()

            // animating the building
//            val animationWorld = MCWorldAnimation(mcWorld, 200)
//            MinecraftForge.EVENT_BUS.register(animationWorld)

            // fancy tower
            val pos = Vec3(posX, mcWorld.getTerrainHeight(posX, posZ), posZ)
            val roofShape = RoofShape.values().random()
            val bodyShape = BodyShape.values().random()
//            val plan = createTurret(
//                origin = pos,
//                size = Vec3(18, 24, 18),
//                roofShape = roofShape,
//                bodyShape = bodyShape
//            ).apply {
//                add4TurretsRecursive()
//            }
//            val plan = createTurret(
//                origin = pos,
//                size = Vec3(12, 32, 12),
//                roofShape = roofShape,
//                bodyShape = bodyShape
//            ).apply {
//                addGrandCastleTurretsRecursive(seed = System.currentTimeMillis())
//            }
//            val plan = createCastleFromTurrets(
//                origin = pos,
//                seed = System.currentTimeMillis()
//            )
//            val plan = createCastleTopDown(
//                origin = pos,
//                seed = System.currentTimeMillis()
//            )
//            val turret = createTurret(
//                origin = Vec3.ZERO,
//                size = Vec3(6, 4, 6),
//                roofShape = RoofShape.FLAT_BORDERED,
//                bodyShape = BodyShape.SQUARE,
//                bottomShape = BottomShape.FOUNDATION,
//                style = TowerStyle(),
//                level = 4
//            )
//            val innerWard = innerWard(turret, 1)
//            val outerWard = outerWard(innerWard, 2)
//            val plan = Structure().apply {
//                origin = pos
//                addChild(outerWard, Vec3(30, 0, 30))
//            }
//            RootBuilder().build(plan, animationWorld, context)

            // test all generated WFC tiles
//            val wfcTiles = generateValidTiles7x3x7()
//            pos.y += 1
//            var tileOffset = 0
//            for (tile in wfcTiles) {
//                for (p in tile) {
//                    val voxel = tile[p]
//                    val blockPos = pos.add(p).addX(tileOffset).toBlockPos()
//                    voxel.mapToBlock()?.let {
//                        val blockState = it.defaultBlockState()
//                        world.setBlock(blockPos, blockState, 2)
//                    }
//                }
//                tileOffset += 8
//            }

            // WFC!!!
            val wfc = WfcTiledModel(10, 8, 10,
                generateValidTiles7x3x7(),
                System.currentTimeMillis()
            ).apply {
                setAirAndGroundBoundary(air, groundedAir, ground)
                observe()
            }
            pos.addLocal(-wfc.width* air.width/2, -air.height, -wfc.depth* air.depth/2)
            for (p in wfc) {
                val tile = wfc[p] ?: continue
                for (v in tile) {
                    val voxel = tile[v]
                    val blockPos = pos
                        .add(v)
                        .add(
                            Vec3(p).apply {
                                x *= air.width
                                y *= air.height
                                z *= air.depth
                            }
                        )
                        .toBlockPos()
                    voxel.mapToBlock()?.let {
                        val blockState = it.defaultBlockState()
                        world.setBlock(blockPos, blockState, 2)
                    }
                }
            }
        }
        return ActionResult.pass(player.getItemInHand(hand))
    }

    private fun WfcColor.mapToBlock(): Block? = when(this) {
        WfcColor.AIR -> null
        WfcColor.WALL -> Blocks.COBBLESTONE
        WfcColor.FLOOR -> Blocks.OAK_PLANKS
        WfcColor.GROUND -> null
    }

}
