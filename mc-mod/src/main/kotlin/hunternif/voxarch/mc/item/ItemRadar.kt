package hunternif.voxarch.mc.item

import com.google.common.annotations.VisibleForTesting
import hunternif.voxarch.world.HeightMap
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.detectMountains
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.event.ClickEvent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World
import java.awt.Color
import java.awt.image.BufferedImage
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
        private const val groundColor = 0xccffcc
        private const val slopeColor = 0xe58066
        private const val topColor = 0xeecc66
        private const val perimeterGroundColor = 0x667fe5
        private const val perimeterSlopeColor = 0x925fa5

        fun HeightMap.print() {
            val mountains = detectMountains()
            minHeight -= 10 // to see the ocean color clearly

            val image = BufferedImage(width, length, BufferedImage.TYPE_INT_RGB)
            for (p in this) {
                val baseColor = when {
                    mountains.any { p in it.perimeter } -> when {
                        mountains.any { p in it.slope } -> perimeterSlopeColor
                        else -> perimeterGroundColor
                    }
                    mountains.any { p in it.slope } -> slopeColor
                    mountains.any { p in it.top } -> topColor
                    else -> groundColor
                }
                val color = getColor(at(p), baseColor)
                image.setRGB(p.x, p.y, color)
            }
            val file = newFile()
            Files.newOutputStream(file).use {
                ImageIO.write(image, "png", it)
            }
            printChatMessage(file)
        }

        @VisibleForTesting
        internal fun HeightMap.getColor(height: Int, baseColor: Int): Int {
            if (maxHeight <= minHeight) return 0x000000
            val color = Color(baseColor)
            val light = 0xff * (height - minHeight) / (maxHeight - minHeight)
            val r = color.red * light / 0xff
            val g = color.green * light / 0xff
            val b = color.blue * light / 0xff
            return (r shl 16) + (g shl 8) + b
        }

        private fun HeightMap.newFile(): Path {
            val dir = Minecraft.getMinecraft().mcDataDir.toPath().resolve("screenshots")
            Files.createDirectories(dir)
            val timestamp = dateFormat.format(Date())
            return dir.resolve("heightmap[${center.x}x${center.y}]_$timestamp.png")
        }

        private fun printChatMessage(file: Path) {
            val text = ChatComponentText(file.fileName.toString()).apply {
                chatStyle.chatClickEvent = ClickEvent(
                    ClickEvent.Action.OPEN_FILE,
                    file.toAbsolutePath().normalize().toString())
                chatStyle.underlined = true
            }
            val message = ChatComponentTranslation("screenshot.success", text)
            Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(message)
        }
    }
}