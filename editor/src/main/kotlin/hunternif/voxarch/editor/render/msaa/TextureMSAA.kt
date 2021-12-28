package hunternif.voxarch.editor.render.msaa

import org.lwjgl.opengl.GL32.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack

class TextureMSAA(private val filepath: String, var samples: Int = 4) {
    var texID = 0

    fun generate(width: Int, height: Int) {
        // Generate texture on GPU
        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, texID)
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, width, height, true)
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0)
    }

    fun resize(width: Int, height: Int) {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, texID)
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, width, height, true)
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0)
    }

    fun load() = MemoryStack.stackPush().use { stack ->
        // Generate texture on GPU
        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, texID)

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // When stretching the image, pixelate
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        // When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        val width = stack.mallocInt(1)
        val height = stack.mallocInt(1)
        val channels = stack.mallocInt(1)
        val image = stbi_load(filepath, width, height, channels, 0)
        if (image != null) {
            val format = if (channels[0] == 3) GL_RGB
            else if (channels[0] == 4) GL_RGBA
            else
                throw RuntimeException("Error: (Texture) Unknown number of channels '${channels[0]}'")
            glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, format, width[0], height[0], true)
        } else {
            throw RuntimeException("Error: (Texture) Could not load image '$filepath'")
        }
        stbi_image_free(image)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, texID)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0)
    }
}