package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Vector4f
import java.nio.file.Path

class PointSpriteShader(
    val texture: Texture,
): Shader() {
    constructor(
        texturePath: Path
    ) : this(Texture(texturePath.toString()))

    override fun init() {
        super.init(
            resourcePath("shaders/point-sprite.vert.glsl"),
            resourcePath("shaders/point-sprite.frag.glsl")
        ) {
            if (!texture.isLoaded) texture.load()
            uploadTexture("uTexSampler", 0)
        }
    }
}