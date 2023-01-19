package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

class MinecraftShader(val texture: Texture): Shader() {
    override fun init() {
        super.init(
            resourcePath("shaders/minecraft.vert.glsl"),
            resourcePath("shaders/minecraft.frag.glsl")
        ) {
            uploadFloat("uDarkenTop", 0.05f)
            uploadFloat("uDarkenZ", 0.25f)
            uploadFloat("uDarkenX", 0.43f)
            uploadFloat("uDarkenBottom", 0.54f)

            uploadMat4f("uModel", Matrix4f())

            if (!texture.isLoaded) texture.load()
            uploadTexture("uTexSampler", 0)
        }
    }

    override fun startFrame() {
        glActiveTexture(GL_TEXTURE0)
        texture.bind()
    }
}