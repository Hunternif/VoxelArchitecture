package hunternif.voxarch.snapshot

import hunternif.voxarch.gen.Generator
import hunternif.voxarch.gen.Materials
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IFixedBlockStorage
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

/**
 * Example: `record(out.sliceZ(1))`
 * -- this will "slice" the storage along the XY plane at z = 1 and render it to a pixel image.
 * @param blockColorMap maps block id to RGB pixel color
 */
abstract class BaseSnapshotTest(
    private val width: Int,
    private val height: Int,
    private val length: Int,
    private val blockColorMap: Map<Int, Int> = DEFAULT_COLORMAP
) {
    @get:Rule
    val name = TestName()

    lateinit var out: MultiDimIntArrayBlockStorage
    lateinit var gen: Generator

    @Before
    fun setup() {
        out = MultiDimIntArrayBlockStorage(width, height, length)
        gen = Generator(out).apply {
            setDefaultMaterials(DEFAULT_MATERIALS)
        }
    }

    fun record(slice: Slice) {
        val path = Paths.get("./out/snapshots/${javaClass.canonicalName}/${name.methodName}.png")
        if (!Files.exists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        ImageIO.write(getImage(slice), "png", Files.newOutputStream(path))
    }

    private fun getImage(slice: Slice):BufferedImage {
        val image = BufferedImage(slice.width, slice.height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until slice.width) {
            for (y in 0 until height) {
                val block = slice.getBlock(x, y)
                val color = blockColorMap.getOrDefault(block?.id ?: ID_AIR, BG_COLOR)
                image.setRGB(x, height - 1 - y, color)
            }
        }
        return image
    }

    interface Slice {
        val width: Int
        val height: Int
        fun getBlock(x: Int, y: Int): BlockData?
    }

    companion object {
        const val BG_COLOR = 0xffffff

        const val ID_AIR = 0
        const val ID_FLOOR = 1
        const val ID_WALL = 2
        const val ID_CEIL = 3

        val DEFAULT_MATERIALS = object : Materials {
            override fun floorBlocks() = arrayOf(BlockData(ID_FLOOR))
            override fun ceilingBlocks() = arrayOf(BlockData(ID_CEIL))
            override fun wallBlocks() = arrayOf(BlockData(ID_WALL))
            override fun gateBlocks() = null
            override fun stairsBlocks(slope: Double) = null
            override fun oneBlockProp(name: String) = null
        }

        val DEFAULT_COLORMAP = mapOf(
            ID_AIR to BG_COLOR,
            ID_FLOOR to 0x593B3C,
            ID_WALL to 0xF0B06C,
            ID_CEIL to 0xB13B42
        )


        fun IFixedBlockStorage.sliceX(offset: Int): Slice {
            val storage = this
            return object : Slice {
                override val width = storage.length
                override val height = storage.height
                override fun getBlock(x: Int, y: Int) =
                    storage.getBlock(offset, y, x)
            }
        }

        fun IFixedBlockStorage.sliceY(offset: Int): Slice {
            val storage = this
            return object : Slice {
                override val width = storage.width
                override val height = storage.length
                override fun getBlock(x: Int, y: Int) =
                    storage.getBlock(x, offset, y)
            }
        }

        fun IFixedBlockStorage.sliceZ(offset: Int): Slice {
            val storage = this
            return object : Slice {
                override val width = storage.width
                override val height = storage.height
                override fun getBlock(x: Int, y: Int) =
                    storage.getBlock(x, y, offset)
            }
        }
    }
}
