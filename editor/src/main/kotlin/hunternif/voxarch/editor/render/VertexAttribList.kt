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

    fun vector3f(id: Int) {
        list.add(VertexAttribData(id, 3, GL_FLOAT, 3 * Float.SIZE_BYTES))
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