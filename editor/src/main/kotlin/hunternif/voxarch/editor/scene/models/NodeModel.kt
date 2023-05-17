package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.box.BoxMesh
import hunternif.voxarch.editor.scene.models.box.BoxTransparentModel
import hunternif.voxarch.editor.scene.models.box.BoxFrameModel
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Matrix4f

class NodeModel(val camera: OrbitalCamera) : IModel {
    private val fillModel = BoxTransparentModel().apply {
        readDepth = true
        writeDepth = false
        // Prevent Z-fighting with voxel models
        shader.depthOffset = 0.001f
    }
    private val lineModel = BoxFrameModel().apply {
        readDepth = true // read from depth buffer
        writeDepth = false // don't write to depth buffer
        shader.depthOffset = -0.001f
    }
    val pointsDebugModel = Points2DModel()

    /** Whether to display debug dots */
    private val DEBUG_NODES = false

    override fun init() {
        fillModel.init()
        lineModel.init()
    }

    fun add(node: SceneNode) {
        fillModel.add(node, node.box.copy(color = node.color.copy(a = 0.1f)))
        lineModel.add(node, node.box.copy(color = node.color.copy(a = 0.2f)))
        if (DEBUG_NODES) {
            pointsDebugModel.add(camera.projectToViewport(node.box.floorCenter))
        }
    }

    fun clear() {
        fillModel.clear()
        lineModel.clear()
        pointsDebugModel.clear()
    }

    fun remove(node: SceneNode) {
        fillModel.remove(node)
        lineModel.remove(node)
    }

    fun update() {
        fillModel.uploadInstanceData()
        lineModel.update()
    }

    override fun runFrame(viewProj: Matrix4f) {
        fillModel.runFrame(viewProj)
        lineModel.runFrame(viewProj)
    }
}