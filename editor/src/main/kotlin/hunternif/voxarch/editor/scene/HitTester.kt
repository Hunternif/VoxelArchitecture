package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.logError
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ByteBufferWrapper
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL32.*

class HitTester(
    val app: EditorApp,
    @PublishedApi internal val camera: OrbitalCamera,
) {
    /** FBO with the texture from which to pick voxel groups. */
    @PublishedApi internal val voxelsFbo = FrameBuffer()

    /**
     * After calling [snapshotVoxelTexture], the texture is written here.
     * The format is RGB, from bottom left corner, row by row.
     */
    private var voxelsFboBytes = ByteBufferWrapper()

    private val voxelGroups = LinkedHashMap<ColorRGBa, SceneVoxelGroup>()

    fun init() {
        voxelsFbo.init(camera.vp)
    }

    /**
     * Returns true if the given point on screen hits this object.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * Optional [resultDistance] stores the distances to the near and far intersection
     *      points.
     * Optional [resultNearPoint] stores the near intersection point.
     */
    fun hitTest(
        obj: SceneObject,
        posX: Number,
        posY: Number,
        resultDistance: Vector2f? = null,
        resultNearPoint: Vector3f? = null,
    ): Boolean {
        val vpX = posX.toFloat() - camera.vp.x
        val vpY = posY.toFloat() - camera.vp.y
        return when (obj) {
            is SceneNode -> camera.projectToBox(
                posX, posY, obj.box, resultDistance, resultNearPoint)
            is SceneVoxelGroup -> hitTestVoxel(vpX, vpY) == obj
            else -> camera.projectToAABox(
                posX, posY, obj.box.aabb.minVec, obj.box.aabb.maxVec,
                resultDistance, resultNearPoint)
        }
    }

    /** Render models onto the pick buffer */
    inline fun renderVoxels(crossinline renderCall: () -> Unit) {
        voxelsFbo.setViewport(camera.vp)
        voxelsFbo.render {
            glClearColor(0f, 0f, 0f, 1f)
            glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
            renderCall()
        }
    }

    /** Associate a voxel group with a picking color. */
    fun registerVoxelGroup(color: ColorRGBa, group: SceneVoxelGroup) {
        voxelGroups[color] = group
    }

    /** Reads pixel data and stores it for hit-testing. */
    fun snapshotVoxelTexture() {
        voxelsFbo.render {
            voxelsFbo.texture.readPixels(voxelsFboBytes)
        }

        // Example of writing an image to PNG.
        // The format is not correct, the result is wrong colors and flipped image.

//        val width = voxelsFbo.texture.width
//        val height = voxelsFbo.texture.height
//        val totalBytes = width * height * voxelsFbo.texture.channels
//
//        val buf = buffer.buffer
//        buf.rewind()
//        val bytes = ByteArray(totalBytes)
//        buf.get(bytes)
//
//        val image = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
//        image.data = Raster.createRaster(
//            image.sampleModel, DataBufferByte(bytes, totalBytes), Point())
//        val path = Paths.get("./voxels_fbo_image.png")
//        Files.newOutputStream(path).use {
//            ImageIO.write(image, "png", it)
//        }
    }

    /** Accepts viewport coordinates, where (0, 0) is top left corner. */
    private fun hitTestVoxel(posX: Number, posY: Number): SceneVoxelGroup? {
        // OpenGL texture uses (0, 0) as bottom left corner
        val x = posX.toInt()
        val y = voxelsFbo.texture.height - posY.toInt()

        val width = voxelsFbo.texture.width
        val height = voxelsFbo.texture.height

        if (x < 0 || x >= width || y < 0 || y >= height) return null

        // Sample the texture
        try {
            val channels = voxelsFbo.texture.channels // should be 3
            val buf = voxelsFboBytes.buffer
            buf.rewind()
            buf.position((y * width + x) * channels)
            val color = ColorRGBa.fromRGBBytes(buf)
            return voxelGroups[color]
        } catch (e: Exception) {
            app.logError(e)
            return null
        }
    }
}