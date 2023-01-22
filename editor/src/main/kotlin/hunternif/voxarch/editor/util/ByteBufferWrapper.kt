package hunternif.voxarch.editor.util

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

/**
 * Allocates off-heap byte buffer using LWJGL MemoryUtil.
 * Reallocates to a bigger buffer if necessary.
 */
class ByteBufferWrapper {
    private var currentSize = 0
    lateinit var buffer: ByteBuffer
        private set

    /**
     * Ensures the inner buffer fits [size] elements,
     * otherwise allocates a new buffer.
     * @return the allocated [ByteBuffer] instance.
     */
    fun prepare(size: Int): ByteBuffer {
        if (!::buffer.isInitialized) {
            currentSize = size * 2
            buffer = MemoryUtil.memAlloc(currentSize)
        }
        if (size > currentSize) {
            if (currentSize == 0) currentSize = 1
            while (size > currentSize) {
                currentSize *= 2
            }
            MemoryUtil.memFree(buffer)
            buffer = MemoryUtil.memAlloc(currentSize)
        }
        buffer.clear()
        return buffer
    }
}