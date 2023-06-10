package hunternif.voxarch.editor.util

import hunternif.voxarch.editor.builder.TexturedBlock
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.forEachFilledPos
import hunternif.voxarch.vector.IntVec3
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.util.*

/**
 * Merges voxels into a single mesh, storing colors in vertices.
 * @return all vertices for all triangle, written to buffer
 */
fun coloredMeshFromVoxelsOpt(
    voxels: IStorage3D<out IVoxel?>,
    colorMap: (IVoxel) -> ColorRGBa,
): FloatBuffer {
    // 1. Find all visible faces.
    val faces = findVisibleFaces(voxels)
    val faceCount = faces.values.fold(0) { out, list -> out + list.size }

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    // 12 = 3f pos + 3f normal + 4f color or UV + 2f AOUV
    val buffer = MemoryUtil.memAllocFloat(faceCount * 2 * 3 * 12)
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val v = voxels[p]!!
            val color = colorMap(v)
            // make vertices for face
            val facePoints = baseFaceVertices[dir]!!
            facePoints.forEachIndexed { i, fp ->
                tempVertArray[i].pos.set(fp).add(p)
                tempVertArray[i].normal.set(dir.vec)
                tempVertArray[i].color = color
            }
            val (v0, v1, v2, v3) = tempVertArray
            // set AO texture UVs:
            val aoTile = aoTiles[0] // white
            v0.uv2.set(aoTile.uvEnd.x, aoTile.uvStart.y)
            v1.uv2.set(aoTile.uvStart.x, aoTile.uvStart.y)
            v2.uv2.set(aoTile.uvStart.x, aoTile.uvEnd.y)
            v3.uv2.set(aoTile.uvEnd.x, aoTile.uvEnd.y)
            // add vertices for triangles:
            buffer.putVertex(v0, VoxelRenderMode.COLORED)
            buffer.putVertex(v1, VoxelRenderMode.COLORED)
            buffer.putVertex(v2, VoxelRenderMode.COLORED)
            buffer.putVertex(v0, VoxelRenderMode.COLORED)
            buffer.putVertex(v2, VoxelRenderMode.COLORED)
            buffer.putVertex(v3, VoxelRenderMode.COLORED)
        }
    }

    return buffer
}

private fun FloatBuffer.putVertex(v: Vertex, renderMode: VoxelRenderMode) {
    put(v.pos)
    put(v.normal)
    when (renderMode) {
        VoxelRenderMode.COLORED -> put(v.color)
        VoxelRenderMode.TEXTURED -> put(v.uv).put(0f).put(0f)
    }
    put(v.uv2)
}

/**
 * Merges voxels into a single mesh, storing texture UVs in vertices.
 * @return all vertices for all triangle, written to buffer
 */
fun texturedMeshFromVoxelsOpt(
    voxels: IStorage3D<out IVoxel?>,
): FloatBuffer {
    // 1. Find all visible faces.
    val faces = findVisibleFaces(voxels)
    val faceCount = faces.values.fold(0) { out, list -> out + list.size }

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    // 12 = 3f pos + 3f normal + 4f color or UV + 2f AOUV
    val buffer = MemoryUtil.memAllocFloat(faceCount * 2 * 3 * 12)
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val uvStart = Vector2f(0f, 0f)
            val uvEnd = Vector2f(1f, 1f)
            val v = voxels[p]!!
            if (v is TexturedBlock) {
                uvStart.set(v.atlasEntry.uvStart)
                uvEnd.set(v.atlasEntry.uvEnd)
            }
            // make vertices for face
            val facePoints = baseFaceVertices[dir]!!
            facePoints.forEachIndexed { i, fp ->
                tempVertArray[i].pos.set(fp).add(p)
                tempVertArray[i].normal.set(dir.vec)
            }
            val (v0, v1, v2, v3) = tempVertArray
            // set block texture UVs:
            v0.uv.set(uvEnd.x, uvStart.y)
            v1.uv.set(uvStart.x, uvStart.y)
            v2.uv.set(uvStart.x, uvEnd.y)
            v3.uv.set(uvEnd.x, uvEnd.y)
            // set AO texture UVs:
            val aoTile = aoTiles[0] // white
            v0.uv2.set(aoTile.uvEnd.x, aoTile.uvStart.y)
            v1.uv2.set(aoTile.uvStart.x, aoTile.uvStart.y)
            v2.uv2.set(aoTile.uvStart.x, aoTile.uvEnd.y)
            v3.uv2.set(aoTile.uvEnd.x, aoTile.uvEnd.y)
            // add vertices for triangles:
            buffer.putVertex(v0, VoxelRenderMode.TEXTURED)
            buffer.putVertex(v1, VoxelRenderMode.TEXTURED)
            buffer.putVertex(v2, VoxelRenderMode.TEXTURED)
            buffer.putVertex(v0, VoxelRenderMode.TEXTURED)
            buffer.putVertex(v2, VoxelRenderMode.TEXTURED)
            buffer.putVertex(v3, VoxelRenderMode.TEXTURED)
        }
    }

    return buffer
}

/** Returns a map from a direction to faces looking into that direction. */
private fun findVisibleFaces(voxels: IStorage3D<out IVoxel?>)
    : Map<Direction3D, LinkedList<IntVec3>> {
    val faces = EnumMap<Direction3D, LinkedList<IntVec3>>(Direction3D::class.java)
    Direction3D.values().forEach { faces[it] = LinkedList() }

    voxels.forEachFilledPos { p, v ->
        Direction3D.values().forEach { dir ->
            // Check neighboring cell
            val pos = p + dir.vec
            if (pos !in voxels || voxels[pos] == null) {
                faces[dir]!!.add(p)
            }
        }
    }
    return faces
}

/** Corners of a box from (-0.5, -0.5, -0.5) to (0.5, 0.5, 0.5). */
private val baseBoxCorners: Array<Vector3f> by lazy {
    val points = arrayOf(
        Vector3f(), Vector3f(), Vector3f(), Vector3f(),
        Vector3f(), Vector3f(), Vector3f(), Vector3f(),
    )
    points[0].set(1f, -1f, 1f)
    points[1].set(1f, -1f, -1f)
    points[2].set(-1f, -1f, -1f)
    points[3].set(-1f, -1f, 1f)
    points[4].set(1f, 1f, 1f)
    points[5].set(1f, 1f, -1f)
    points[6].set(-1f, 1f, -1f)
    points[7].set(-1f, 1f, 1f)
    points.forEach { it.mul(0.5f) }
    points
}

private val baseFaceVertices by lazy {
    baseBoxCorners.let {
        mapOf(
            UP    to arrayOf(it[6], it[7], it[4], it[5]),
            DOWN  to arrayOf(it[3], it[2], it[1], it[0]),
            EAST  to arrayOf(it[5], it[4], it[0], it[1]),
            SOUTH to arrayOf(it[4], it[7], it[3], it[0]),
            WEST  to arrayOf(it[7], it[6], it[2], it[3]),
            NORTH to arrayOf(it[6], it[5], it[1], it[2]),
        )
    }
}


/** Temporary array of 4 vertices to be reused */
private val tempVertArray = arrayOf(Vertex(), Vertex(), Vertex(), Vertex())
