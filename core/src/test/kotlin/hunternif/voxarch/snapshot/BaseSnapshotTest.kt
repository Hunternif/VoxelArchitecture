package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.BaseBuilderTest
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.floor
import hunternif.voxarch.storage.IFixedBlockStorage
import hunternif.voxarch.util.Slice
import hunternif.voxarch.util.XSlice
import hunternif.voxarch.util.YSlice
import hunternif.voxarch.util.ZSlice
import hunternif.voxarch.vector.Vec3
import org.junit.Rule
import org.junit.rules.TestName
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

/**
 * Example: `record(out.sliceZ(1))`
 * -- this will "slice" the storage along the XY plane at z = 1 and render it to a pixel image.
 * @param blockColorMap maps block id to RGB pixel color
 */
abstract class BaseSnapshotTest(
    width: Int,
    height: Int,
    length: Int,
    private val blockColorMap: Map<String, Int> = DEFAULT_COLORMAP
) : BaseBuilderTest(width, height, length) {
    @get:Rule
    val name = TestName()

    fun record(slice: Slice) {
        val path = SNAPSHOTS_DIR.resolve("${javaClass.canonicalName}/${name.methodName}.png")
        if (!Files.exists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        Files.newOutputStream(path).use {
            ImageIO.write(getImage(slice), "png", it)
        }
    }

    private fun getImage(slice: Slice):BufferedImage {
        val image = BufferedImage(slice.width, slice.height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until slice.width) {
            for (y in 0 until slice.height) {
                val block = slice.getBlock(x, y)
                val color = blockColorMap.getOrDefault(block?.key ?: ID_AIR, BG_COLOR)
                image.setRGB(x, slice.height - 1 - y, color)
            }
        }
        return image
    }

    fun Node.ground() {
        floor(Vec3.ZERO, Vec3(width - 1, 0, length - 1)).apply {
            type = TYPE_FLOOR
        }
    }

    companion object {
        val SNAPSHOTS_DIR: Path = Paths.get("./out/snapshots")

        const val BG_COLOR = 0xffffff

        val DEFAULT_COLORMAP = mapOf(
            ID_AIR to BG_COLOR,
            ID_FLOOR to 0x593B3C,
            ID_WALL to 0xF0B06C,
            ID_ROOF to 0xB13B42
        )

        fun IFixedBlockStorage.sliceX(offset: Int): Slice = XSlice(this, offset)
        fun IFixedBlockStorage.sliceY(offset: Int): Slice = YSlice(this, offset)
        fun IFixedBlockStorage.sliceZ(offset: Int): Slice = ZSlice(this, offset)
    }
}
