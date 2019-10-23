package hunternif.voxarch.mc

import hunternif.voxarch.vector.Vec3
import net.minecraft.block.Block
import net.minecraft.util.BlockPos

fun BlockPos.toVec3() = Vec3(x, y, z)

val Block.id: Int get() = Block.getIdFromBlock(this)