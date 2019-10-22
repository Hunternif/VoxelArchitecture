package hunternif.voxarch.mc

import com.google.common.annotations.VisibleForTesting
import hunternif.voxarch.vector.IntVec2
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.World
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

class HeightMap(
    val width: Int,
    val length: Int
) {
    private val map = Array(width) { IntArray(length) }
    var minHeight = 0
    var maxHeight = 256
    var center: IntVec2 = IntVec2(0, 0)

    fun print() {
        val image = BufferedImage(width, length, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until width) {
            for (z in 0 until length) {
                val color = getColor(map[x][z])
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
    internal fun getColor(height: Int): Int {
        if (maxHeight <= minHeight) return 0x000000
        val b = 0xff * (height - minHeight) / (maxHeight - minHeight)
        return b*4/5 + (b shl 8) + (b*4/5 shl 16)
    }

    private fun newFile(): Path {
        val dir = Minecraft.getMinecraft().mcDataDir.toPath().resolve("screenshots")
        Files.createDirectories(dir)
        val timestamp = dateFormat.format(Date())
        return dir.resolve("heightmap[${center.x}x${center.y}]_$timestamp.png")
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")

        /**
         * Returns a snapshot of the world's height map around [center] of size [area].
         */
        fun World.heightMap(center: IntVec2, area: IntVec2): HeightMap {
            val start = IntVec2(center.x - (area.x-1)/2, center.y - (area.y-1)/2)
            return HeightMap(area.x, area.y).apply {
                this.center = center
                minHeight = height
                maxHeight = 0
                for (x in 0 until area.x) {
                    for (z in 0 until area.y) {
                        val height = getPrecipitationHeight(
                            BlockPos(start.x + x, 0, start.y + z)
                        ).y
                        map[x][z] = height
                        if (height < minHeight) minHeight = height
                        if (height > maxHeight) maxHeight = height
                    }
                }
            }
        }

        /**
         * Returns a snapshot of the world's height map around [center] of size [area],
         * ignoring non-terrain blocks.
         */
        fun World.terrainMap(center: IntVec2, area: IntVec2): HeightMap {
            val start = IntVec2(center.x - (area.x-1)/2, center.y - (area.y-1)/2)
            val env = MCEnvironment.environment
            return HeightMap(area.x, area.y).apply {
                this.center = center
                minHeight = height
                maxHeight = 0
                for (x in 0 until area.x) {
                    for (z in 0 until area.y) {
                        var top = getPrecipitationHeight(
                            BlockPos(start.x + x, 0, start.y + z)
                        )
                        while (top.y > 0) {
                            val blockId = Block.getIdFromBlock(getBlockState(top).block)
                            if (blockId in env.buildThroughBlocks) top = top.down()
                            else break
                        }
                        map[x][z] = top.y
                        if (top.y < minHeight) minHeight = top.y
                        if (top.y > maxHeight) maxHeight = top.y
                    }
                }
            }
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