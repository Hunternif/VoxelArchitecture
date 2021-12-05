package hunternif.voxarch.magicavoxel

import com.scs.voxlib.GridPoint3
import com.scs.voxlib.VoxFile
import com.scs.voxlib.VoxWriter
import com.scs.voxlib.Voxel
import com.scs.voxlib.chunk.*
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3


// I don't plan to support materials, only colors for now.

data class VoxColor(val color: Int)

class VoxFileStorage private constructor(
    private val array: Array3D<VoxColor?>
) : IStorage3D<VoxColor?> by array {

    private val palette = LinkedHashSet<VoxColor>()
    private val colorIndex = mutableMapOf<VoxColor, Byte>()
    private var voxelCount = 0

    constructor(width: Int, height: Int, length: Int):
        this(Array3D<VoxColor?>(width, height, length, null))

    override operator fun set(p: IntVec3, v: VoxColor?) { set(p.x, p.y, p.z, v) }
    override operator fun set(x: Int, y: Int, z: Int, v: VoxColor?) {
        if (v == null) {
            if (array[x, y, z] != null) voxelCount--
        } else {
            if (array[x, y, z] == null) voxelCount++
            palette.add(v)
        }
        array[x, y, z] = v
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
            // + 1 because Magica Voxel colors start with index 1
            colorIndex[color] = (i + 1).toByte()
        }
    }

    private fun makeSizeChunk() = VoxSizeChunk(
        IntVec3(width, height, length).toGridPoint3()
    )
    private fun makeVoxelChunk() = VoxXYZIChunk(voxelCount).apply {
        var i = 0
        array.forEachIndexed { pos, color ->
            val index = colorIndex[color]
            if (index != null) {
                voxels[i] = Voxel(pos.toGridPoint3(), index)
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
            // + 1 because Magica Voxel colors start with index 1
            chunk.palette[(i + 1) % 256] = color.color
        }
        return chunk
    }
}

fun IntVec3.toGridPoint3() = GridPoint3(z, x, y)