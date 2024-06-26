package hunternif.voxarch.editor.util

import hunternif.voxarch.editor.builder.TexturedBlock
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.forEachFilledPos
import hunternif.voxarch.util.opposite
import hunternif.voxarch.vector.IntVec3
import org.joml.Vector2f
import org.joml.Vector3f
import java.util.EnumMap

/**
 * Merges voxels into a single mesh, storing colors in vertices.
 * @return a list of vertices per triangle
 */
fun coloredMeshFromVoxels(
    voxels: IStorage3D<out IVoxel?>,
    colorMap: (IVoxel) -> ColorRGBa,
): List<Vertex> {
    // 1. Find all visible faces.
    val faces = findVisibleFaces(voxels)
    val faceCount = faces.values.fold(0) { out, list -> out + list.size }

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    val allVertices = ArrayList<Vertex>(faceCount * 2 * 3)
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val v = voxels[p]!!
            val color = colorMap(v)
            val (v0, v1, v2, v3) = makeVerticesForVoxelFace(p, dir)
            v0.color = color
            v1.color = color
            v2.color = color
            v3.color = color
            // add vertices for triangles:
            allVertices.add(v0)
            allVertices.add(v1)
            allVertices.add(v2)
            allVertices.add(v0)
            allVertices.add(v2)
            allVertices.add(v3)
        }
    }

    return allVertices
}

/**
 * Merges voxels into a single mesh, storing texture UVs in vertices.
 * @return a list of vertices per triangle
 */
fun texturedMeshFromVoxels(
    voxels: IStorage3D<out IVoxel?>,
): List<Vertex> {
    // 1. Find all visible faces.
    val faces = findVisibleFaces(voxels)
    val faceCount = faces.values.fold(0) { out, list -> out + list.size }

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    val allVertices = ArrayList<Vertex>(faceCount * 2 * 3)
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val (v0, v1, v2, v3) = makeVerticesForVoxelFace(p, dir)
            val uvStart = Vector2f(0f, 0f)
            val uvEnd = Vector2f(1f, 1f)
            val v = voxels[p]!!
            if (v is TexturedBlock) {
                uvStart.set(v.atlasEntry.uvStart)
                uvEnd.set(v.atlasEntry.uvEnd)
            }
            v0.uv.set(uvEnd.x, uvStart.y)
            v1.uv.set(uvStart.x, uvStart.y)
            v2.uv.set(uvStart.x, uvEnd.y)
            v3.uv.set(uvEnd.x, uvEnd.y)
            // add vertices for triangles:
            allVertices.add(v0)
            allVertices.add(v1)
            allVertices.add(v2)
            allVertices.add(v0)
            allVertices.add(v2)
            allVertices.add(v3)
        }
    }

    return allVertices
}

/** Returns a map from a direction to faces looking into that direction. */
private fun findVisibleFaces(voxels: IStorage3D<out IVoxel?>)
    : Map<Direction3D, LinkedHashSet<IntVec3>> {
    val faces = EnumMap<Direction3D, LinkedHashSet<IntVec3>>(Direction3D::class.java)
    Direction3D.values().forEach { faces[it] = linkedSetOf() }

    // For each voxel, add faces that are visible to the map.
    // If a new voxel obscures an existing face, delete it.
    voxels.forEachFilledPos { p, _ ->
        values().forEach { dir ->
            val oppositeFaces = faces[dir.opposite()]!!
            val oppositePoint = p + dir.vec
            if (oppositePoint !in oppositeFaces) {
                faces[dir]!!.add(p)
            } else {
                oppositeFaces.remove(oppositePoint)
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

/**
 * Creates 4 vertices for a face of a voxel, ordered CCW.
 * @param p position of the voxel.
 * @param dir direction of the face.
 */
fun makeVerticesForVoxelFace(p: IntVec3, dir: Direction3D): Array<Vertex> {
    val points = baseFaceVertices[dir]!!
    points.forEachIndexed { i, v ->
        tempVertArray[i] = Vertex(Vector3f(v).add(p))
    }
    tempVertArray.forEach { it.normal.set(dir.vec) }
    return tempVertArray
}

/** Assuming vertices to be ordered CCW. */
fun makeFaceTriangles(vertices: List<Vertex>): Array<Triangle> {
    val (v0, v1, v2, v3) = vertices
    return arrayOf(
        Triangle(v0, v1, v2),
        Triangle(v0, v2, v3),
    )
}