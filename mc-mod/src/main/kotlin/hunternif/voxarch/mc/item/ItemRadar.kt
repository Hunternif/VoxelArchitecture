package hunternif.voxarch.mc.item

import hunternif.voxarch.mc.HeightMap.Companion.terrainMap
import hunternif.voxarch.vector.IntVec2
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemRadar : Item() {
    private val radius = 64

    init {
        maxStackSize = 1
    }

    override fun addInformation(stack: ItemStack, player: EntityPlayer, tooltip: MutableList<Any?>, advanced: Boolean) {
        tooltip.add("Scan heightmap of surroundings")
    }

    override fun onItemRightClick(
        stack: ItemStack, world: World, player: EntityPlayer
    ): ItemStack {
        if (world.isRemote) {
            val map = world.terrainMap(
                IntVec2(player.posX.toInt(), player.posZ.toInt()),
                IntVec2(radius*2 + 1, radius*2 + 1)
            )
            map.print()
        }
        return stack
    }
}