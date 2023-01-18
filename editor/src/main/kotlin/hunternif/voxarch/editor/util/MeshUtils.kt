package hunternif.voxarch.editor.util

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.forEachFilledPos
import hunternif.voxarch.util.opposite
import hunternif.voxarch.vector.IntVec3
import org.joml.Vector3f
import java.util.EnumMap

/** Merges voxels into a single mesh, preserving vertex colors. */
fun meshFromVoxels(
    voxels: IStorage3D<out IVoxel?>,
    colorMap: (IVoxel) -> ColorRGBa,
): Mesh {
    // 1. Find all visible faces.

    /** Remembers positions of all faces per direction. */
    val faces = EnumMap<Direction3D, LinkedHashSet<IntVec3>>(Direction3D::class.java)
    values().forEach { faces[it] = linkedSetOf() }

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

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    val mesh = Mesh()
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val v = voxels[p]!!
            val color = colorMap(v)
            val triangles = makeTrianglesForVoxelFace(p, dir)
            triangles.forEach { t -> t.vertices.forEach { it.color = color } }
            mesh.triangles.addAll(triangles)
        }
    }

    return mesh
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
            UP to arrayOf(it[5], it[7], it[4], it[6]),
            DOWN to arrayOf(it[0], it[2], it[1], it[3]),
            EAST to arrayOf(it[1], it[4], it[0], it[5]),
            SOUTH to arrayOf(it[0], it[7], it[3], it[4]),
            WEST to arrayOf(it[3], it[6], it[2], it[7]),
            NORTH to arrayOf(it[2], it[5], it[1], it[6]),
        )
    }
}

/**
 * Creates 2 new triangles for a face of a voxel.
 * @param p position of the voxel.
 * @param dir direction of the face.
 */
fun makeTrianglesForVoxelFace(p: IntVec3, dir: Direction3D): Array<Triangle> {
    val points = baseFaceVertices[dir]!!
    val vertices = points.map { Vertex(Vector3f(it).add(p)) }
    val (v0, v1, v2, v3) = vertices
    val (p0, p1, p2) = points
    val normal = Vector3f(p1).sub(p0).cross(Vector3f(p2).sub(p0)).normalize()
    vertices.forEach { it.normal.set(normal) }
    return arrayOf(
        Triangle(v0, v1, v2),
        Triangle(v0, v3, v1),
    )
}