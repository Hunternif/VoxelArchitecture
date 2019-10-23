package hunternif.voxarch.mc.item

import com.google.common.annotations.VisibleForTesting
import hunternif.voxarch.world.HeightMap
import hunternif.voxarch.world.HeightMap.Companion.terrainMap
import hunternif.voxarch.mc.MCWorld
import hunternif.voxarch.vector.IntVec2
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.event.ClickEvent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.World
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

        fun HeightMap.print() {
            val image = BufferedImage(width, length, BufferedImage.TYPE_INT_RGB)
            for (x in 0 until width) {
                for (z in 0 until length) {
                    val color = getColor(at(x, z))
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
        internal fun HeightMap.getColor(height: Int): Int {
            if (maxHeight <= minHeight) return 0x000000
            val b = 0xff * (height - minHeight) / (maxHeight - minHeight)
            return b*4/5 + (b shl 8) + (b*4/5 shl 16)
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