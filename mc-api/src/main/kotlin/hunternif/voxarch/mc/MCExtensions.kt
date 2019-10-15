package hunternif.voxarch.mc

import hunternif.voxarch.vector.Vec3
import net.minecraft.util.BlockPos

fun BlockPos.toVec3() = Vec3(x, y, z)
