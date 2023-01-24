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
import hunternif.voxarch.vector.IntVec3


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
) : IStorage3D<VoxColor?> by data {

    private val palette = LinkedHashSet<VoxColor>()
        // add the 0 color to shift the index. MagicaVoxel uses indices 1+.
        .also { it.add(VoxColor(0)) }
    private val colorIndex = mutableMapOf<VoxColor, Byte>()
    private var voxelCount = 0

    override operator fun set(p: IntVec3, v: VoxColor?) { set(p.x, p.y, p.z, v) }
    override operator fun set(x: Int, y: Int, z: Int, v: VoxColor?) {
        if (v == null) {
            if (data[x, y, z] != null) voxelCount--
        } else {
            if (data[x, y, z] == null) voxelCount++
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

            // 3. Transform for the following 'group' chunk?
            appendChunk(makeTransformChunk(0, 1))
            // 4. group chunk, references the following 'transform' chunk
            appendChunk(makeGroupChunk(1, 2))

            // 5. Transform for the following 'shape' chunk.
            appendChunk(makeTransformChunk(2, 3))
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
                voxels[i] = Voxel(gridPoint3(x, y, z), index)
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

        /** Only reads 1 model */
        fun fromFile(file: VoxFile): VoxFileStorage {
            val model = file.modelInstances.first().model
            val width = model.size.y
            val height = model.size.z
            val length = model.size.x
            val storage = VoxFileStorage()
            file.palette.forEach {
                //TODO check if VoxFileParser stores colors correctly
                val unsignedColor = it and 0xffffff
                storage.palette.add(VoxColor(unsignedColor))
            }
            model.voxels.forEach {
                val pos = it.position.toIntVec3()
                val color = file.palette[it.colourIndex] and 0xffffff
                storage.data[pos] = VoxColor(color)
            }
            return storage
        }
    }
}