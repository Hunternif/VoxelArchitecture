package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector4f

class SolidColorShader(
    color: ColorRGBa = Colors.debug,
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
            updateColor(color)
        }
    }

    var color: ColorRGBa = color
        private set

    /** Must be called when shader is in use. */
    fun updateColor(newColor: ColorRGBa) {
        color = newColor
        uploadVec4f("uColor", Vector4f(color.r, color.g, color.b, color.a))
    }

    /** This is a cheat to prevent Z-fighting of lines on top of voxels. */
    var depthOffset: Float = 0f
        set(value) {
            field = value
            if (isInitialized) {
                use {
                    uploadFloat("depthOffset", value)
                }
            }
        }
}