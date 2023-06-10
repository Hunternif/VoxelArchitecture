package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.ByteBufferWrapper
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.safeClear
import hunternif.voxarch.editor.util.safeFlip
import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.*
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

class Texture(val filepath: Path) {
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
    var format: Int = GL_RGB
        private set

    private val pixelBuffer = MemoryUtil.memAlloc(4)

    fun generate(width: Int, height: Int) {
        this.width = width
        this.height = height
        this.format = GL_RGB
        this.channels = 3
        // Generate texture on GPU
        texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texID)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexImage2D(
            GL_TEXTURE_2D, 0, format, width, height,
            0, format, GL_UNSIGNED_BYTE, 0
        )
        isLoaded = true
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
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
//        stbi_image_free(bytes)
    }

    /**
     * Loads the texture bytes from file, without uploading it to the GPU.
     * This also sets parameters [width], [height], [channels].
     */
    fun loadData(): ByteBuffer = MemoryStack.stackPush().use { stack ->
        // STBI only works with files in directories:
//        val width = stack.mallocInt(1)
//        val height = stack.mallocInt(1)
//        val channels = stack.mallocInt(1)
//        val bytes = stbi_load(filepath.toString(), width, height, channels, 0)
//            ?: throw RuntimeException("Error: (Texture) Could not load image '$filepath'")
        // BufferedImage works with files in JAR too:
        val inputStream = Files.newInputStream(filepath)
        val image = ImageIO.read(inputStream)
        this.width = image.width
        this.height = image.height
        this.channels = image.channels
        if (this.channels != 3 && this.channels != 4) {
            throw RuntimeException("Error: (Texture) Unknown number of channels '${this.channels}'")
        }
        // Thanks to https://stackoverflow.com/a/54294080/1093712
        val pixels = image.getRGB(0, 0, image.width, image.height, null, 0, image.width)
        val buffer = ByteBuffer.allocateDirect(pixels.size * channels)
        for (pixel in pixels) {
            if (channels == 3) {
                buffer.put((pixel shr 16 and 0xFF).toByte())
                buffer.put((pixel shr 8 and 0xFF).toByte())
                buffer.put((pixel and 0xFF).toByte())
            } else if (channels == 4) {
                buffer.put((pixel shr 16 and 0xFF).toByte())
                buffer.put((pixel shr 8 and 0xFF).toByte())
                buffer.put((pixel and 0xFF).toByte())
                buffer.put((pixel shr 24 and 0xFF).toByte())
            }
        }
        buffer.safeFlip()
        return buffer
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
            format = GL_RGB
            glTexImage2D(
                GL_TEXTURE_2D, 0, format,
                width, height,
                0, format, GL_UNSIGNED_BYTE, bytes
            )
        } else if (channels == 4) {
            format = GL_RGBA
            glTexImage2D(
                GL_TEXTURE_2D, 0, format,
                width, height,
                0, format, GL_UNSIGNED_BYTE, bytes
            )
        }
        isLoaded = true
    }

    /** Reads data from the GPU */
    fun readPixels(bufferWrapper: ByteBufferWrapper) {
        val buffer = bufferWrapper.prepare(width * height * channels)
        // this fixes image skewing at certain sizes, see answer:
        // https://stackoverflow.com/questions/73498632/photo-generated-by-glreadpixels-is-broken
        glPixelStorei(GL_PACK_ALIGNMENT, 1)
        glReadPixels(0, 0, width, height, format, GL_UNSIGNED_BYTE, buffer)
    }

    /** Reads a single pixel from the GPU */
    fun readPixel(x: Int, y: Int): ColorRGBa {
        if (x < 0 || x >= width || y < 0 || y >= height) return Colors.debug
        pixelBuffer.safeClear()
        glReadPixels(x, y, 1, 1, format, GL_UNSIGNED_BYTE, pixelBuffer)
        return ColorRGBa.fromRGBBytes(pixelBuffer)
    }


    /** Set OpenGL to sample from this texture. */
    fun bind() {
        glBindTexture(GL_TEXTURE_2D, texID)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}

/** Get number of channels */
// Thanks to https://stackoverflow.com/a/21464485/1093712
private val BufferedImage.channels: Int get() = when (type) {
    TYPE_INT_RGB -> 3
    TYPE_INT_ARGB -> 4
    TYPE_INT_ARGB_PRE -> 4
    TYPE_INT_BGR -> 3
    TYPE_3BYTE_BGR -> 3
    TYPE_4BYTE_ABGR -> 4
    TYPE_4BYTE_ABGR_PRE -> 4
    TYPE_USHORT_565_RGB -> 3
    TYPE_USHORT_555_RGB -> 3
    TYPE_BYTE_GRAY -> 1
    TYPE_USHORT_GRAY -> 1
    else -> 0
}

/**
 * Renders a texture onto a frame buffer.
 */
fun copyTexture(
    source: Texture,
    sourceUVStart: Vector2f,
    sourceUVEnd: Vector2f,
    targetFbo: FrameBuffer,
    targetCoordStart: Vector2i,
    targetCoordEnd: Vector2i,
) = targetFbo.render {
    val target = targetFbo.texture

    glEnable(GL_TEXTURE_2D)
    glDisable(GL_LIGHTING)
    glDisable(GL_BLEND)
    glDisable(GL_DEPTH_TEST)
    glColor4f(1f, 1f, 1f, 1f)

    source.bind()
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

    glViewport(0, 0, target.width, target.height)
    glMatrixMode(GL_PROJECTION)
    glPushMatrix()
    glLoadIdentity()
    glOrtho(0.0, target.width.toDouble(), 0.0, target.height.toDouble(), 0.0, 100.0)

    glBegin(GL_QUADS)
    glTexCoord2f(sourceUVEnd.x, sourceUVStart.y)
    glVertex3i(targetCoordEnd.x, targetCoordStart.y, 0)
    glTexCoord2f(sourceUVEnd.x, sourceUVEnd.y)
    glVertex3i(targetCoordEnd.x, targetCoordEnd.y, 0)
    glTexCoord2f(sourceUVStart.x, sourceUVEnd.y)
    glVertex3i(targetCoordStart.x, targetCoordEnd.y, 0)
    glTexCoord2f(sourceUVStart.x, sourceUVStart.y)
    glVertex3i(targetCoordStart.x, targetCoordStart.y, 0)
    glEnd()

    glPopMatrix()
}