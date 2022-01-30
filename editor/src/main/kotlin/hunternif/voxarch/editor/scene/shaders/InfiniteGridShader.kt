package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Matrix4f

class InfiniteGridShader(
    private val showAxes: Boolean = false,
): Shader() {
    override fun init() {
        super.init(
            resourcePath("shaders/infinite-grid.vert.glsl"),
            resourcePath("shaders/infinite-grid.frag.glsl")
        ) {
            // Offset the grid by -0.5
            uploadMat4f("uModel", Matrix4f().translate(-0.5f, -0.5f, -0.5f))

            uploadFloat("uNearFade", 0.01f)
            uploadFloat("uFarFade", 50f)

            uploadVec4f("uGridColor", ColorRGBa.fromHex(0x333333, 0.5f).toVector4f())
            uploadVec4f("uAxisXColor", Colors.axisX.copy(a=0.5f).toVector4f())
            uploadVec4f("uAxisZColor", Colors.axisZ.copy(a=0.5f).toVector4f())

            uploadBool("showAxes", showAxes)
        }
    }
}