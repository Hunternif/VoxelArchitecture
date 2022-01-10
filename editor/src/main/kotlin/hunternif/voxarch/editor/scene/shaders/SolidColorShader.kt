package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Matrix4f

class SolidColorShader(private val colorHex: Int): Shader() {
    fun init() {
        super.init(
            resourcePath("shaders/solid-color.vert.glsl"),
            resourcePath("shaders/solid-color.frag.glsl")
        ) {
            uploadVec3f("uColor", ColorRGBa.fromHex(colorHex).toVector3f())
        }
    }

    inline fun render(viewProj: Matrix4f, crossinline action: () -> Unit) {
        use {
            uploadMat4f("uViewProj", viewProj)
            action()
        }
    }
}