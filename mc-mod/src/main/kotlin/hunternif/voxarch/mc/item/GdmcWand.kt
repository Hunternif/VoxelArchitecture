package hunternif.voxarch.mc.item

import hunternif.voxarch.mc.gdmc.buildSettlement
import hunternif.voxarch.mc.gdmc.defaultRadius
import hunternif.voxarch.mc.toVec3
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Util
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World

class GdmcWand(properties: Properties) : Item(properties) {

    override fun appendHoverText(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<ITextComponent>,
        flag: ITooltipFlag
    ) {
        tooltip.add(StringTextComponent("Build a settlement for GDMC"))
    }

    override fun use(
        world: World, player: PlayerEntity, hand: Hand
    ): ActionResult<ItemStack> {
        if (!world.isClientSide) {
            val radius = defaultRadius

            val pos = player.blockPosition().toVec3()
            val from = pos.add(-radius, 0.0, -radius).toIntVec3()
            val to = pos.add(radius, 0.0, radius).toIntVec3()

            player.chatMessage("Will build from $from to $to")
            player.chatMessage("Building...")
            buildSettlement(world, from.toXZ(), to.toXZ())
            player.chatMessage("Finished building")
        }
        return ActionResult.pass(player.getItemInHand(hand))
    }

    private fun Entity.chatMessage(msg: String) {
        sendMessage(StringTextComponent(msg), Util.NIL_UUID)
    }

}
