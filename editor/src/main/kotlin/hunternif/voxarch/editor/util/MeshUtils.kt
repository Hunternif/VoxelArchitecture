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

/** Merges voxels into a single mesh, storing colors in vertices. */
fun coloredMeshFromVoxels(
    voxels: IStorage3D<out IVoxel?>,
    colorMap: (IVoxel) -> ColorRGBa,
): Mesh {
    // 1. Find all visible faces.
    val faces = findVisibleFaces(voxels)

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    val mesh = Mesh()
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val v = voxels[p]!!
            val color = colorMap(v)
            val vertices = makeVerticesForVoxelFace(p, dir)
            vertices.forEach { it.color = color }
            mesh.triangles.addAll(makeFaceTriangles(vertices))
        }
    }

    return mesh
}

/** Merges voxels into a single mesh, storing texture UVs in vertices. */
fun texturedMeshFromVoxels(
    voxels: IStorage3D<out IVoxel?>,
): Mesh {
    // 1. Find all visible faces.
    val faces = findVisibleFaces(voxels)

    // 2. Reconstruct the mesh by creating triangles for each visible face.
    val mesh = Mesh()
    faces.forEach { (dir, points) ->
        points.forEach { p ->
            val vertices = makeVerticesForVoxelFace(p, dir)
            val uvStart = Vector2f(0f, 0f)
            val uvEnd = Vector2f(1f, 1f)
            val v = voxels[p]!!
            if (v is TexturedBlock) {
                uvStart.set(v.atlasEntry.uvStart)
                uvEnd.set(v.atlasEntry.uvEnd)
            }
            vertices.let {
                it[0].uv.set(uvEnd.x, uvStart.y)
                it[1].uv.set(uvStart.x, uvStart.y)
                it[2].uv.set(uvStart.x, uvEnd.y)
                it[3].uv.set(uvEnd.x, uvEnd.y)
            }
            mesh.triangles.addAll(makeFaceTriangles(vertices))
        }
    }

    return mesh
}

/** Returns a map from a direction to faces looking into that direction. */
private fun findVisibleFaces(voxels: IStorage3D<out IVoxel?>)
    : Map<Direction3D, LinkedHashSet<IntVec3>> {
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

/**
 * Creates 4 vertices for a face of a voxel, ordered CCW.
 * @param p position of the voxel.
 * @param dir direction of the face.
 */
fun makeVerticesForVoxelFace(p: IntVec3, dir: Direction3D): List<Vertex> {
    val points = baseFaceVertices[dir]!!
    val vertices = points.map { Vertex(Vector3f(it).add(p)) }
    val (p0, p1, p2) = points
    val normal = Vector3f(p1).sub(p0).cross(Vector3f(p2).sub(p0)).normalize()
    vertices.forEach { it.normal.set(normal) }
    return vertices
}

/** Assuming vertices to be ordered CCW. */
fun makeFaceTriangles(vertices: List<Vertex>): Array<Triangle> {
    val (v0, v1, v2, v3) = vertices
    return arrayOf(
        Triangle(v0, v1, v2),
        Triangle(v0, v2, v3),
    )
}