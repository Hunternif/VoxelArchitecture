package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Texture
import org.lwjgl.opengl.EXTTextureFilterAnisotropic.*
import org.lwjgl.opengl.GL33.*

abstract class VoxelShader : Shader() {
    var renderMode: VoxelRenderMode = VoxelRenderMode.COLORED
        set(value) {
            field = value
            if (isInitialized) {
                use {
                    uploadInt("uRenderMode", value.id)
                }
            }
        }

    var texture: Texture? = null
    var aoTexture: Texture? = null

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

    override fun startFrame() {
        if (aoTexture != null) {
            glActiveTexture(GL_TEXTURE1)
            aoTexture?.bind()
            // When shrinking the image, interpolate
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            // When stretching an image, pixelate
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            // Anisotropic filtering
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 16f)
            // Reset texture index, otherwise it breaks:
            glActiveTexture(GL_TEXTURE0)
        }
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