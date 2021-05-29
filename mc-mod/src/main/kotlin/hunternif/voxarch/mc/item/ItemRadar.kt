package hunternif.voxarch.mc.item

import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.sandbox.image
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.HeightMap
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.world.World
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.floor

class ItemRadar(properties: Properties) : Item(properties) {
    private val radius = 64

    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<ITextComponent>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(StringTextComponent("Scan heightmap of surroundings"))
    }

    override fun onItemRightClick(
        world: World, player: PlayerEntity, hand: Hand
    ): ActionResult<ItemStack> {
        if (world.isRemote) {
            val mcWorld = MCWorld(world)
            val map = mcWorld.terrainMap(
                IntVec2(floor(player.posX).toInt(), floor(player.posZ).toInt()),
                IntVec2(radius*2 + 1, radius*2 + 1)
            )
            map.minHeight = mcWorld.seaLevel
            map.print()
        }
        return ActionResult.resultPass(player.getHeldItem(hand))
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")

        fun HeightMap.print() {
            val image = image()
            val file = newFile()
            Files.newOutputStream(file).use {
                ImageIO.write(image, "png", it)
            }
            printChatMessage(file)
        }

        private fun HeightMap.newFile(): Path {
            val dir = Minecraft.getInstance().gameDir.toPath().resolve("screenshots")
            Files.createDirectories(dir)
            val timestamp = dateFormat.format(Date())
            return dir.resolve("heightmap[${center.x}x${center.y}]_$timestamp.png")
        }

        private fun printChatMessage(file: Path) {
            val text = StringTextComponent(file.fileName.toString()).apply {
                applyTextStyle(TextFormatting.UNDERLINE)
                applyTextStyle {
                    it.clickEvent = ClickEvent(
                        ClickEvent.Action.OPEN_FILE,
                        file.toAbsolutePath().normalize().toString()
                    )
                }
            }
            val message = TranslationTextComponent("screenshot.success", text)
            Minecraft.getInstance().ingameGUI.chatGUI.printChatMessage(message)
        }
    }
}