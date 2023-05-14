package hunternif.voxarch.editor.util

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

/**
 * Allocates off-heap float buffer using LWJGL MemoryUtil.
 * Reallocates to a bigger buffer if necessary.
 */
class FloatBufferWrapper {
    private var currentSize = 0
    lateinit var buffer: FloatBuffer
        private set

    /**
     * Ensures the inner buffer fits [size] elements,
     * otherwise allocates a new buffer.
     * @return the allocated [FloatBuffer] instance.
     */
    fun prepare(size: Int): FloatBuffer {
        if (!::buffer.isInitialized) {
            currentSize = size * 2
            buffer = MemoryUtil.memAllocFloat(currentSize)
        }
        if (size > currentSize) {
            if (currentSize == 0) currentSize = 1
            while (size > currentSize) {
                currentSize *= 2
            }
            MemoryUtil.memFree(buffer)
            buffer = MemoryUtil.memAllocFloat(currentSize)
        }
        buffer.safeClear()
        return buffer
    }
}