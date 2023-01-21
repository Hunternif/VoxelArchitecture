package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Texture
import org.lwjgl.opengl.EXTTextureFilterAnisotropic.*
import org.lwjgl.opengl.GL33.*

abstract class VoxelShader : Shader() {
    var renderMode: VoxelRenderMode = VoxelRenderMode.COLORED
        private set

    var texture: Texture? = null

    /** Must be called when shader is in use. */
    fun updateRenderMode(newMode: VoxelRenderMode) {
        renderMode = newMode
        uploadInt("uRenderMode", newMode.id)
    }

    override fun startFrame() {
        if (renderMode == VoxelRenderMode.TEXTURED) {
            glActiveTexture(GL_TEXTURE0)
            texture?.bind()
            // When shrinking the image, interpolate
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            // When stretching an image, pixelate
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            // Anisotropic filtering
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 16f)
        }
    }
}

enum class VoxelRenderMode(val id: Int) {
    COLORED(1),
    TEXTURED(2),
}

enum class VoxelShadingMode {
    MAGICA_VOXEL,
    MINECRAFT,
}