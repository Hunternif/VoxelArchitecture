package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.BaseBuilderTest
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.ArrayBlockStorage
import hunternif.voxarch.storage.BlockData
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
 * TODO: in Y slice, the Z axis on the image is inverted, compared to Minecraft.
 * @param keyToColorMap maps block key to RGB pixel color
 */
abstract class BaseSnapshotTest(
    outWidth: Int,
    outHeight: Int,
    outLength: Int,
    private val keyToColorMap: Map<String, Int> = DEFAULT_COLORMAP
) : BaseBuilderTest(outWidth, outHeight, outLength) {
    @get:Rule
    val name = TestName()

    private val blockToVoxColorMap by lazy {
        keyToColorMap
            .mapKeys { (k, v) -> BlockData(k) }
            .mapValues { (k, v) -> VoxColor(v) }
    }

    /** Records a slice of the output into a PNG image.
     * These can be useful to quickly spot the error. */
    fun record(slice: Slice) {
        val path = SNAPSHOTS_DIR.resolve(
            "${javaClass.canonicalName}/${name.methodName} ${slice.getName()}.png"
        )
        if (!Files.exists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        Files.newOutputStream(path).use {
            ImageIO.write(getImage(slice), "png", it)
        }
    }

    /** Records the entire output into a VOX file. */
    fun recordVox() {
        val path = SNAPSHOTS_DIR.resolve("${javaClass.canonicalName}/${name.methodName}.vox")
        if (!Files.exists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        Files.newOutputStream(path).use {
            out.writeToVoxFile(path, blockToVoxColorMap)
        }
    }

    private fun getImage(slice: Slice):BufferedImage {
        val image = BufferedImage(slice.width, slice.height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until slice.width) {
            for (y in 0 until slice.height) {
                val block = slice.getBlock(x, y)
                val color = keyToColorMap.getOrDefault(block?.key ?: ID_AIR, BG_COLOR)
                image.setRGB(x, slice.height - 1 - y, color)
            }
        }
        return image
    }

    fun Node.ground() {
        room(Vec3.ZERO, Vec3(outWidth - 1, 0, outLength - 1)) {
            floor {
                type = TYPE_FLOOR
            }
        }
    }

    companion object {
        val SNAPSHOTS_DIR: Path = Paths.get("./out/snapshots")

        const val BG_COLOR = 0xffffff

        val DEFAULT_COLORMAP = mapOf(
            ID_AIR to BG_COLOR,
            ID_FLOOR to 0x593B3C,
            ID_WALL to 0xF0B06C,
            ID_ROOF to 0xB13B42,
            ID_WALL_DECO to 0xAE977D
        )

        fun ArrayBlockStorage.sliceX(offset: Int): Slice = XSlice(this, offset)
        fun ArrayBlockStorage.sliceY(offset: Int): Slice = YSlice(this, offset)
        fun ArrayBlockStorage.sliceZ(offset: Int): Slice = ZSlice(this, offset)
    }
}
