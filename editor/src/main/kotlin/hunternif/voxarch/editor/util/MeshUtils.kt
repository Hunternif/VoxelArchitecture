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

/**
 * Creates 2 new triangles for a face of a voxel.
 * @param p position of the voxel.
 * @param dir direction of the face.
 */
fun makeTrianglesForVoxelFace(p: IntVec3, dir: Direction3D): Array<Triangle> {
    val triangles = baseBoxCorners.let {
        when (dir) {
            UP -> arrayOf(
                Triangle.asCopy(it[4], it[5], it[7]),
                Triangle.asCopy(it[5], it[6], it[7]),
            )
            DOWN -> arrayOf(
                Triangle.asCopy(it[0], it[2], it[1]),
                Triangle.asCopy(it[0], it[3], it[2]),
            )
            EAST -> arrayOf(
                Triangle.asCopy(it[1], it[4], it[0]),
                Triangle.asCopy(it[1], it[5], it[4]),
            )
            SOUTH -> arrayOf(
                Triangle.asCopy(it[0], it[4], it[7]),
                Triangle.asCopy(it[0], it[7], it[3]),
            )
            WEST -> arrayOf(
                Triangle.asCopy(it[3], it[6], it[2]),
                Triangle.asCopy(it[3], it[7], it[6]),
            )
            NORTH -> arrayOf(
                Triangle.asCopy(it[1], it[2], it[5]),
                Triangle.asCopy(it[2], it[6], it[5]),
            )
        }
    }
    triangles.forEach { t -> t.vertices.forEach { it.pos.add(p) } }
    return triangles
}