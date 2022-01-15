package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.util.Edge
import hunternif.voxarch.editor.util.Vertex
import org.joml.Vector3f

/** Borrowed from LearnOpenGL.com */
val boxVertices = listOf(
    // coordinates        // normals
    // back face
    Vertex(0f, 0f, 0f,  0.0f,  0.0f, -1.0f),
    Vertex(0f, 1f, 0f,  0.0f,  0.0f, -1.0f),
    Vertex(1f, 1f, 0f,  0.0f,  0.0f, -1.0f),
    Vertex(1f, 1f, 0f,  0.0f,  0.0f, -1.0f),
    Vertex(1f, 0f, 0f,  0.0f,  0.0f, -1.0f),
    Vertex(0f, 0f, 0f,  0.0f,  0.0f, -1.0f),

    // front face
    Vertex(0f, 0f, 1f,  0.0f,  0.0f,  1.0f),
    Vertex(1f, 0f, 1f,  0.0f,  0.0f,  1.0f),
    Vertex(1f, 1f, 1f,  0.0f,  0.0f,  1.0f),
    Vertex(1f, 1f, 1f,  0.0f,  0.0f,  1.0f),
    Vertex(0f, 1f, 1f,  0.0f,  0.0f,  1.0f),
    Vertex(0f, 0f, 1f,  0.0f,  0.0f,  1.0f),

    // left face
    Vertex(0f, 1f, 1f, -1.0f,  0.0f,  0.0f),
    Vertex(0f, 1f, 0f, -1.0f,  0.0f,  0.0f),
    Vertex(0f, 0f, 0f, -1.0f,  0.0f,  0.0f),
    Vertex(0f, 0f, 0f, -1.0f,  0.0f,  0.0f),
    Vertex(0f, 0f, 1f, -1.0f,  0.0f,  0.0f),
    Vertex(0f, 1f, 1f, -1.0f,  0.0f,  0.0f),

    // right face
    Vertex(1f, 1f, 1f,  1.0f,  0.0f,  0.0f),
    Vertex(1f, 0f, 1f,  1.0f,  0.0f,  0.0f),
    Vertex(1f, 0f, 0f,  1.0f,  0.0f,  0.0f),
    Vertex(1f, 0f, 0f,  1.0f,  0.0f,  0.0f),
    Vertex(1f, 1f, 0f,  1.0f,  0.0f,  0.0f),
    Vertex(1f, 1f, 1f,  1.0f,  0.0f,  0.0f),

    // bottom face
    Vertex(0f, 0f, 0f,  0.0f, -1.0f,  0.0f),
    Vertex(1f, 0f, 0f,  0.0f, -1.0f,  0.0f),
    Vertex(1f, 0f, 1f,  0.0f, -1.0f,  0.0f),
    Vertex(1f, 0f, 1f,  0.0f, -1.0f,  0.0f),
    Vertex(0f, 0f, 1f,  0.0f, -1.0f,  0.0f),
    Vertex(0f, 0f, 0f,  0.0f, -1.0f,  0.0f),

    // top face
    Vertex(0f, 1f, 0f,  0.0f,  1.0f,  0.0f),
    Vertex(0f, 1f, 1f,  0.0f,  1.0f,  0.0f),
    Vertex(1f, 1f, 1f,  0.0f,  1.0f,  0.0f),
    Vertex(1f, 1f, 1f,  0.0f,  1.0f,  0.0f),
    Vertex(1f, 1f, 0f,  0.0f,  1.0f,  0.0f),
    Vertex(0f, 1f, 0f,  0.0f,  1.0f,  0.0f),
)

fun boxEdges(start: Vector3f, end: Vector3f): List<Edge> {
    // bottom vertices
    val v1 = Vector3f(start.x, start.y, start.z)
    val v2 = Vector3f(start.x, start.y, end.z)
    val v3 = Vector3f(end.x, start.y, end.z)
    val v4 = Vector3f(end.x, start.y, start.z)
    
    // top vertices
    val v5 = Vector3f(start.x, end.y, start.z)
    val v6 = Vector3f(start.x, end.y, end.z)
    val v7 = Vector3f(end.x, end.y, end.z)
    val v8 = Vector3f(end.x, end.y, start.z)

    return listOf(
        // bottom rectangle
        Edge(v1, v2),
        Edge(v2, v3),
        Edge(v3, v4),
        Edge(v4, v1),

        // top rectangle
        Edge(v5, v6),
        Edge(v6, v7),
        Edge(v7, v8),
        Edge(v8, v5),

        // vertical edges
        Edge(v1, v5),
        Edge(v2, v6),
        Edge(v3, v7),
        Edge(v4, v8),
    )
}