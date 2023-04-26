package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector4f

/**
 * Courtesy of http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
 * Renders the silhouette of a model using the given color.
 */
class VoxelPickShader(
    color: ColorRGBa = Colors.debug,
): Shader() {

    override fun init() {
        super.init(
            resourcePath("shaders/voxel-pick.vert.glsl"),
            resourcePath("shaders/voxel-pick.frag.glsl")
        ) {
            updateColor(color)
        }
    }

    var color: ColorRGBa = color
        private set

    /** Must be called when shader is in use. */
    fun updateColor(newColor: ColorRGBa) {
        color = newColor
        use {
            uploadVec4f("uPickingColor", Vector4f(color.r, color.g, color.b, color.a))
        }
    }
}