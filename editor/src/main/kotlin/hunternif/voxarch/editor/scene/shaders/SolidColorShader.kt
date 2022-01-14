package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector4f

class SolidColorShader(
    private val colorHex: Int,
    private val alpha: Float = 1f,
): Shader() {
    override fun init() {
        super.init(
            resourcePath("shaders/solid-color.vert.glsl"),
            resourcePath("shaders/solid-color.frag.glsl")
        ) {
            val color = ColorRGBa.fromHex(colorHex)
            uploadVec4f("uColor", Vector4f(color.r, color.g, color.b, alpha))
        }
    }
}