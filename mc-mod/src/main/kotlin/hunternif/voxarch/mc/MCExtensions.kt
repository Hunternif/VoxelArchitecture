package hunternif.voxarch.mc

import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraftforge.registries.ForgeRegistries

fun BlockPos.toVec3() = Vec3(x, y, z)
fun BlockPos.toXZ() = IntVec2(x, z)
fun Vec3.toIntVec3() = IntVec3(x, y, z)
fun Vec3.toBlockPos() = BlockPos(x, y, z)

val Block.key: String get() = ForgeRegistries.BLOCKS.getKey(this).toString()