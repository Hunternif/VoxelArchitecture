package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL32.*

class HitTester(
    @PublishedApi internal val camera: OrbitalCamera,
) {
    /** FBO with the texture from which to pick voxel groups. */
    val voxelsFbo = FrameBuffer()
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

    /** Accepts viewport coordinates, where (0, 0) is top left corner. */
    private fun hitTestVoxel(posX: Number, posY: Number): SceneVoxelGroup? {
        // OpenGL texture uses (0, 0) as bottom left corner
        val x = posX.toInt()
        val y = voxelsFbo.texture.height - posY.toInt()
        var color: ColorRGBa = Colors.debug
        voxelsFbo.render {
            color = voxelsFbo.texture.readPixel(x, y)
        }
        return voxelGroups[color]
    }
}