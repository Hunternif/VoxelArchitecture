package hunternif.voxarch.magicavoxel

import com.scs.voxlib.GridPoint3
import com.scs.voxlib.VoxFile
import com.scs.voxlib.VoxWriter
import com.scs.voxlib.Voxel
import com.scs.voxlib.chunk.*
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.forEachPos
import hunternif.voxarch.vector.IntAABB
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3


// I don't plan to support materials, only colors for now.

data class VoxColor(val color: Int) : IVoxel {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VoxColor) return false
        return color == other.color
    }
    override fun hashCode(): Int = color
}

class VoxFileStorage(
    private val data: IStorage3D<VoxColor?> = ChunkedStorage3D()
) : IStorage3D<VoxColor?> {

    private val palette = LinkedHashSet<VoxColor>()
        // add the 0 color to shift the index. MagicaVoxel uses indices 1+.
        .also { it.add(VoxColor(0)) }
    private val colorIndex = mutableMapOf<VoxColor, Byte>()
    private var voxelCount = 0

    private val container = IntAABB()

    override val minX: Int get() = container.minX
    override val minY: Int get() = container.minY
    override val minZ: Int get() = container.minZ
    override val maxX: Int get() = container.maxX
    override val maxY: Int get() = container.maxY
    override val maxZ: Int get() = container.maxZ

    constructor(width: Int, height: Int, depth: Int): this() {
        container.setMin(0, 0, 0)
        container.setMax(width - 1, height - 1, depth - 1)
    }

    override val size: Int get() = data.size
    override fun get(x: Int, y: Int, z: Int): VoxColor? = data[x, y, z]
    override operator fun set(p: IntVec3, v: VoxColor?) { set(p.x, p.y, p.z, v) }
    override operator fun set(x: Int, y: Int, z: Int, v: VoxColor?) {
        if (v == null) {
            if (data[x, y, z] != null) voxelCount--
        } else {
            if (data[x, y, z] == null) voxelCount++
            container.union(x, y, z)
            palette.add(v)
        }
        data[x, y, z] = v
    }

    fun serialize(): VoxFile {
        refreshColorIndex()
        val root = VoxRootChunk().apply {
            // 1. Define size of the whole project
            appendChunk(makeSizeChunk())

            // 2. Add voxel positions for the 'model'
            appendChunk(makeVoxelChunk())
            //TODO: split chunks bigger than 256, because VOX stores each voxel
            // coordinate in 256 bits

            // 3. Transform for the following 'group' chunk?
            appendChunk(makeTransformChunk(0, 1))
            // 4. group chunk, references the following 'transform' chunk
            appendChunk(makeGroupChunk(1, 2))

            // 5. Transform for the following 'shape' chunk.
            // the transform vector points from (0, 0, 0) to model center:
            val size = IntVec3(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1)
            val transform = (IntVec3(minX, minY, minZ) + size / 2).toGridPoint3()
            appendChunk(makeTransformChunk(2, 3, transform))
            // 6. 'Shape' chunk, references 'model'
            appendChunk(makeShapeChunk(3, 0))

            // 7. layers...

            // 8. Palette with indexed ABGR colors
            appendChunk(makePaletteChunk())

            // 9. Materials...
        }
        return VoxFile(VoxWriter.VERSION, root)
    }

    private fun refreshColorIndex() {
        colorIndex.clear()
        palette.forEachIndexed { i, color ->
            colorIndex[color] = i.toByte()
        }
    }

    private fun makeSizeChunk() = VoxSizeChunk(
        IntVec3(width, height, depth).toGridPoint3()
    )
    private fun makeVoxelChunk() = VoxXYZIChunk(voxelCount).apply {
        var i = 0
        data.forEachPos { x, y, z, color ->
            val index = colorIndex[color]
            if (index != null) {
                // offset voxels from negative coordinates
                voxels[i] = Voxel(gridPoint3(x - minX, y - minY, z - minZ), index)
                i++
            }
        }
    }
    private fun makeTransformChunk(
        id: Int,
        shapeId: Int,
        transform: GridPoint3 = GridPoint3()
    ) = VoxTransformChunk(id).apply {
        this.child_node_id = shapeId
        this.transform = transform
    }
    private fun makeGroupChunk(id: Int, childId: Int) = VoxGroupChunk(id).apply {
        this.child_ids.add(childId)
    }
    private fun makeShapeChunk(id: Int, modelId: Int) = VoxShapeChunk(id).apply {
        this.model_ids.add(modelId)
    }
    private fun makePaletteChunk(): VoxRGBAChunk {
        val chunk = VoxRGBAChunk()
        palette.forEachIndexed { i, color ->
            chunk.palette[i % 256] = color.color
        }
        return chunk
    }

    companion object {
        private fun GridPoint3.toIntVec3() = IntVec3(y, z, x)
        private fun IntVec3.toGridPoint3() = GridPoint3(z, x, y)
        private fun gridPoint3(x: Int, y: Int, z: Int) = GridPoint3(z, x, y)

        /**
         * Reads all models and places them at their offsets in a single
         * combined storage.
         * @param useModelOffset use VOX model instance offsets to determine
         * position and size. If false, will place models in the corner by default.
         */
        fun fromFile(file: VoxFile, useModelOffset: Boolean = true): VoxFileStorage {
            val storage = VoxFileStorage()
            file.palette.forEach {
                val unsignedColor = it and 0xffffff
                storage.palette.add(VoxColor(unsignedColor))
            }
            file.modelInstances.forEach { modelInst ->
                val model = modelInst.model
                val size = model.size.toIntVec3()
                var cornerOffset = IntVec3(0, 0, 0)
                if (useModelOffset) {
                    // In the file, voxel positions are stored relative to model's corner.
                    // worldOffset is the distance to model's center from scene origin (0, 0, 0).
                    // VoxFileStorage will match (0, 0, 0) with the file's (0, 0, 0).
                    cornerOffset = modelInst.worldOffset.toIntVec3() - size / 2
                    storage.container.union(cornerOffset)
                    storage.container.union(size + cornerOffset - IntVec3(1, 1, 1))
                }
                model.voxels.forEach {
                    val pos = it.position.toIntVec3().addLocal(cornerOffset)
                    val color = file.palette[it.colourIndex and 0xff] and 0xffffff
                    storage.data[pos] = VoxColor(color)
                    storage.container.union(pos)
                }
            }
            return storage
        }
    }
}