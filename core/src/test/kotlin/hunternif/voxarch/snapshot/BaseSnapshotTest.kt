package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.floor
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IFixedBlockStorage
import hunternif.voxarch.storage.MultiDimIntArrayBlockStorage
import hunternif.voxarch.vector.Vec3
import org.junit.Before
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
    private val width: Int,
    private val height: Int,
    private val length: Int,
    private val blockColorMap: Map<Int, Int> = DEFAULT_COLORMAP
) {
    @get:Rule
    val name = TestName()

    lateinit var out: MultiDimIntArrayBlockStorage
    lateinit var buildContext: BuildContext
    lateinit var builder: Builder<Node>

    @Before
    fun setup() {
        out = MultiDimIntArrayBlockStorage(width, height, length)
        buildContext = BuildContext()
        builder = Builder()
        setupDefaultMaterials()
        setupDefaultBuilders()
    }

    fun build(node: Node) = builder.build(node, out, buildContext)

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

    private fun setupDefaultMaterials() {
        buildContext.materials.apply {
            set(MaterialConfig.FLOOR) { BlockData(ID_FLOOR) }
            set(MaterialConfig.WALL) { BlockData(ID_WALL) }
            set(MaterialConfig.ROOF) { BlockData(ID_ROOF) }
        }
    }

    private fun setupDefaultBuilders() {
        buildContext.builders.apply {
            set(
                TYPE_FLOOR to SimpleFloorBuilder(MaterialConfig.FLOOR),
                TYPE_ROOF to SimpleFloorBuilder(MaterialConfig.ROOF),
                null to SimpleFloorBuilder(MaterialConfig.FLOOR)
            )
            setDefault(SimpleWallBuilder(MaterialConfig.WALL))
            setDefault(SimpleGateBuilder())
            setDefault(SimpleHatchBuilder())
            setDefault<Node>(Builder())
        }
    }

    fun Node.ground() {
        floor(Vec3.ZERO, Vec3(width - 1, 0, length - 1)).apply {
            type = TYPE_FLOOR
        }
    }

    companion object {
        val SNAPSHOTS_DIR: Path = Paths.get("./out/snapshots")

        const val BG_COLOR = 0xffffff

        const val TYPE_FLOOR = "floor"
        const val TYPE_ROOF = "roof"

        const val ID_AIR = 0
        const val ID_FLOOR = 1
        const val ID_WALL = 2
        const val ID_ROOF = 3

        val DEFAULT_COLORMAP = mapOf(
            ID_AIR to BG_COLOR,
            ID_FLOOR to 0x593B3C,
            ID_WALL to 0xF0B06C,
            ID_ROOF to 0xB13B42
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
