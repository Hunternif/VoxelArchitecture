package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector4f

class SolidColorShader(
    private val color: ColorRGBa
): Shader() {
    constructor(
        colorHex: Int,
        alpha: Float = 1f,
    ): this(ColorRGBa.fromHex(colorHex, alpha))

    override fun init() {
        super.init(
            resourcePath("shaders/solid-color.vert.glsl"),
            resourcePath("shaders/solid-color.frag.glsl")
        ) {
            uploadVec4f("uColor", Vector4f(color.r, color.g, color.b, color.a))
        }
    }
}