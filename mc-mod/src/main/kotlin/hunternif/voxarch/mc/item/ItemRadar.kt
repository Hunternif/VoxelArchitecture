package hunternif.voxarch.mc.item

import com.google.common.annotations.VisibleForTesting
import hunternif.voxarch.world.HeightMap
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.detectMountains
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.event.ClickEvent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.World
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

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
            val mcWorld = MCWorld(world)
            val map = mcWorld.terrainMap(
                IntVec2(player.posX.toInt(), player.posZ.toInt()),
                IntVec2(radius*2 + 1, radius*2 + 1)
            )
            map.minHeight = mcWorld.seaLevel
            map.print()
        }
        return stack
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")
        private const val normalColor = 0xccffcc
        private const val mountColor = 0xe58066

        fun HeightMap.print() {
            val mountains = detectMountains()

            val image = BufferedImage(width, length, BufferedImage.TYPE_INT_RGB)
            for (x in 0 until width) {
                for (z in 0 until length) {
                    val baseColor =
                        if (mountains.any { it.contains(IntVec2(x, z)) }) mountColor
                        else normalColor
                    val color = getColor(at(x, z), baseColor)
                    image.setRGB(x, z, color)
                }
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