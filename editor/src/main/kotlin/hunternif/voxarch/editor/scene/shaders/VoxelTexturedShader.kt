package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*

class VoxelTexturedShader(val texture: Texture): Shader() {
    override fun init() {
        super.init(
            resourcePath("shaders/voxel-textured.vert.glsl"),
            resourcePath("shaders/voxel-textured.frag.glsl")
        ) {
            // Skylight falls uniformly in this direction
            uploadVec3f("uSkylightDir", Vector3f(-0.77f, -1.0f, -0.9f).normalize())
            uploadVec3f("uSkylightColor", ColorRGBa.fromHex(0xffffff).toVector3f())
            uploadFloat("uSkylightPower", 1.25f)

            // Backlight highlights the bottom of the model
            uploadVec3f("uBacklightDir", Vector3f(0.77f, 1.0f, 0.9f).normalize())
            uploadVec3f("uBacklightColor", ColorRGBa.fromHex(0xffffff).toVector3f())
            uploadFloat("uBacklightPower", 1.0f)

            uploadVec3f("uAmbientColor", ColorRGBa.fromHex(0x353444).toVector3f())
            uploadFloat("uAmbientPower", 1.0f)

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