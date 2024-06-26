package hunternif.voxarch.editor.render

import org.lwjgl.opengl.GL33.*

class VertexAttribList {
    data class VertexAttribData(
        val id: Int,
        val size: Int,
        val glType: Int,
        val sizeBytes: Int
    )

    val list = mutableListOf<VertexAttribData>()

    fun vector2f(id: Int) {
        list.add(VertexAttribData(id, 2, GL_FLOAT, 2 * Float.SIZE_BYTES))
    }

    fun vector3f(id: Int) {
        list.add(VertexAttribData(id, 3, GL_FLOAT, 3 * Float.SIZE_BYTES))
    }

    fun vector4f(id: Int) {
        list.add(VertexAttribData(id, 4, GL_FLOAT, 4 * Float.SIZE_BYTES))
    }

    /** Matrix4 takes up 4 consecutive id values. */
    fun mat4f(id: Int) {
        list.add(VertexAttribData(id, 4, GL_FLOAT, 4 * Float.SIZE_BYTES))
        list.add(VertexAttribData(id+1, 4, GL_FLOAT, 4 * Float.SIZE_BYTES))
        list.add(VertexAttribData(id+2, 4, GL_FLOAT, 4 * Float.SIZE_BYTES))
        list.add(VertexAttribData(id+3, 4, GL_FLOAT, 4 * Float.SIZE_BYTES))
    }

    fun upload() {
        val stride = list.fold(0) { s, attr -> s + attr.sizeBytes }
        var offset = 0L
        for (attr in list) {
            glEnableVertexAttribArray(attr.id)
            glVertexAttribPointer(attr.id, attr.size, attr.glType, false, stride, offset)
            offset += attr.sizeBytes
        }
    }
}