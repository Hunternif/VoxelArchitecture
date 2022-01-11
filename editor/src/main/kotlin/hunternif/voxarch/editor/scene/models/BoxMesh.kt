package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.util.Vertex

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