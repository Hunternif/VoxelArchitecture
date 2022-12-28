package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.ceiling
import hunternif.voxarch.plan.floor
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.ArrayBlockStorage
import hunternif.voxarch.vector.TransformationStack
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.world.Environment
import org.junit.Before

abstract class BaseBuilderTest(
    internal val outWidth: Int,
    internal val outHeight: Int,
    internal val outLength: Int
) {
    lateinit var out: ArrayBlockStorage
    lateinit var trans: TransformationStack
    lateinit var context: BuildContext
    lateinit var builder: Builder<Node>

    @Before
    open fun setup() {
        out = ArrayBlockStorage(outWidth, outHeight, outLength)
//        out.safeBoundary = true
        trans = TransformationStack()
        context = BuildContext(DEFAULT_ENV)
        builder = Builder()
        setupDefaultMaterials()
        setupDefaultBuilders()
    }

    fun build(node: Node) {
        try {
            builder.build(node, trans, out, context)
        } catch (e: ArrayIndexOutOfBoundsException) {
            // don't throw, so we can see the results in the snapshot
            e.printStackTrace()
        }
    }

    private fun setupDefaultMaterials() {
        context.materials.apply {
            set(MAT_FLOOR) { BlockData(ID_FLOOR) }
            set(MAT_WALL) { BlockData(ID_WALL) }
            set(MAT_ROOF) { BlockData(ID_ROOF) }
            set(MAT_WALL_DECORATION) { BlockData(ID_WALL_DECO) }
        }
    }

    private fun setupDefaultBuilders() {
        context.builders.apply {
            set(
                TYPE_FLOOR to SimpleFloorBuilder(MAT_FLOOR),
                TYPE_ROOF to SimpleFloorBuilder(MAT_ROOF),
                null to SimpleFloorBuilder(MAT_FLOOR)
            )
            setDefault(SimpleWallBuilder(MAT_WALL))
            setDefault(RoomBuilder())
            setDefault(SimpleGateBuilder())
            setDefault(SimpleHatchBuilder())
            setDefault<Node>(Builder())
        }
    }

    fun testRoom(origin: Vec3, size: Vec3) =
        Room(null, origin, size, 0.0).apply {
            floor { tags += TYPE_FLOOR }
            ceiling { tags += TYPE_ROOF }
            createFourWalls()
        }

    companion object {
        const val TYPE_FLOOR = "floor"
        const val TYPE_ROOF = "roof"

        const val ID_AIR = "0"
        const val ID_FLOOR = "1"
        const val ID_WALL = "2"
        const val ID_ROOF = "3"
        const val ID_WALL_DECO = "4"

        val DEFAULT_ENV = object : Environment {
            override val minY: Int = 0
            override fun isTerrain(block: BlockData?): Boolean = true
            override fun shouldBuildThrough(block: BlockData?): Boolean = false
        }
    }
}