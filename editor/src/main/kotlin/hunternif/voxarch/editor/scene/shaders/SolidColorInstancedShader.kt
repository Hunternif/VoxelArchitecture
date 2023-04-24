package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Matrix4f

class SolidColorInstancedShader: Shader() {
    override fun init() {
        super.init(
            resourcePath("shaders/solid-color-instanced.vert.glsl"),
            resourcePath("shaders/solid-color-instanced.frag.glsl")
        ) {
            uploadMat4f("uModel", Matrix4f())

            uploadBool("useDepthOffset", useDepthOffset)
        }
    }

    /** This is a cheat to prevent Z-fighting of lines on top of voxels. */
    var useDepthOffset: Boolean = false
        set(value) {
            field = value
            if (isInitialized) {
                use {
                    uploadBool("useDepthOffset", value)
                }
            }
        }
}