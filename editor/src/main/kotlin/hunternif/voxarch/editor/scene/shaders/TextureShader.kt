package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.util.resourcePath
import org.lwjgl.opengl.GL33.*
import java.nio.file.Path

class TextureShader(
    val texture: Texture,
): Shader() {
    constructor(
        texturePath: Path
    ) : this(Texture(texturePath))

    override fun init() {
        super.init(
            resourcePath("shaders/texture.vert.glsl"),
            resourcePath("shaders/texture.frag.glsl")
        ) {
            if (!texture.isLoaded) texture.load()
            uploadTexture("uTexSampler", 0)
        }
    }

    override fun startFrame() {
        glActiveTexture(GL_TEXTURE0)
        texture.bind()
    }
}