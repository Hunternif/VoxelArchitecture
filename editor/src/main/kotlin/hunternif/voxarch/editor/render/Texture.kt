package hunternif.voxarch.editor.render

import org.lwjgl.opengl.GL32.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class Texture(val filepath: String) {
    /** This texture's ID for OpenGL */
    var texID = 0

    /** For file-based textures, returns true if the file is loaded into video memory. */
    var isLoaded = false
        private set

    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var channels: Int = 0
        private set

    fun generate(width: Int, height: Int) {
        this.width = width
        this.height = height
        this.channels = 3
        // Generate texture on GPU
        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texID)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGB, width, height,
            0, GL_RGB, GL_UNSIGNED_BYTE, 0
        )
        isLoaded = true
    }

    fun resize(width: Int, height: Int) {
        glBindTexture(GL_TEXTURE_2D, texID)
        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGB, width, height,
            0, GL_RGB, GL_UNSIGNED_BYTE, 0
        )
    }

    /** Loads the texture from file and uploads it to the GPU. */
    fun load() {
        val bytes = loadData()
        uploadToGPU(bytes)
        stbi_image_free(bytes)
    }

    /**
     * Loads the texture bytes from file, without uploading it to the GPU.
     * This also sets parameters [width], [height], [channels].
     */
    fun loadData(): ByteBuffer = MemoryStack.stackPush().use { stack ->
        val width = stack.mallocInt(1)
        val height = stack.mallocInt(1)
        val channels = stack.mallocInt(1)
        val bytes = stbi_load(filepath, width, height, channels, 0)
            ?: throw RuntimeException("Error: (Texture) Could not load image '$filepath'")
        this.width = width[0]
        this.height = height[0]
        this.channels = channels[0]
        if (this.channels != 3 && this.channels != 4) {
            throw RuntimeException("Error: (Texture) Unknown number of channels '${this.channels}'")
        }
        return bytes
    }

    /** Uploads to GPU.
     * Assuming that [width], [height], [channels] have been set. */
    fun uploadToGPU(bytes: ByteBuffer) {
        // Generate texture on GPU
        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texID)

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // When shrinking the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        // When stretching an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        if (channels == 3) {
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, bytes
            )
        } else if (channels == 4) {
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGBA,
                width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, bytes
            )
        }
        isLoaded = true
    }


    /** Set OpenGL to sample from this texture. */
    fun bind() {
        glBindTexture(GL_TEXTURE_2D, texID)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}