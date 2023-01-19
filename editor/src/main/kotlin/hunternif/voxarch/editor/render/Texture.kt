package hunternif.voxarch.editor.render

import org.lwjgl.opengl.GL32.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack

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

    fun generate(width: Int, height: Int) {
        this.width = width
        this.height = height
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

    fun load() = MemoryStack.stackPush().use { stack ->
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
        val width = stack.mallocInt(1)
        val height = stack.mallocInt(1)
        val channels = stack.mallocInt(1)
        val image = stbi_load(filepath, width, height, channels, 0)
        if (image != null) {
            this.width = width[0]
            this.height = height[0]
            if (channels[0] == 3) {
                glTexImage2D(
                    GL_TEXTURE_2D, 0, GL_RGB,
                    width[0], height[0],
                    0, GL_RGB, GL_UNSIGNED_BYTE, image
                )
            } else if (channels[0] == 4) {
                glTexImage2D(
                    GL_TEXTURE_2D, 0, GL_RGBA,
                    width[0], height[0],
                    0, GL_RGBA, GL_UNSIGNED_BYTE, image
                )
            } else {
                throw RuntimeException("Error: (Texture) Unknown number of channels '${channels[0]}'")
            }
        } else {
            throw RuntimeException("Error: (Texture) Could not load image '$filepath'")
        }
        stbi_image_free(image)
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