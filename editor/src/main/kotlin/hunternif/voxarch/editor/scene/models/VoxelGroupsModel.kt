package hunternif.voxarch.editor.scene.models

import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.IModel
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.scene.shaders.*
import hunternif.voxarch.editor.scene.shaders.VoxelShadingMode.*
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.storage.IVoxel
import org.joml.Matrix4f
import org.lwjgl.opengl.GL32.*

/** Contains all models for individual [SceneVoxelGroup] instances. */
class VoxelGroupsModel(
    private val vp: Viewport,
    private val colorMap: (IVoxel) -> ColorRGBa,
) : IModel {
    private val models = LinkedHashMap<SceneVoxelGroup, VoxelMeshModel>()

    private val minecraftShader = MinecraftShader()
    private val magicaVoxelShader = MagicaVoxelShader()

    private var selectedShader: VoxelShader = magicaVoxelShader

    val selectionFbo = FrameBuffer()

    var shadingMode: VoxelShadingMode = MAGICA_VOXEL
        private set

    override fun init() {
        minecraftShader.init()
        magicaVoxelShader.init()
        selectionFbo.init(vp)
    }

    fun setShadingMode(mode: VoxelShadingMode) {
        shadingMode = mode
        selectedShader = when (mode) {
            MAGICA_VOXEL -> magicaVoxelShader
            MINECRAFT -> minecraftShader
        }
        models.forEach { (_, v) -> v.shader = selectedShader }
    }

    /** Creates model and voxel mesh. */
    private fun getModel(group: SceneVoxelGroup) = models.getOrPut(group) {
        VoxelMeshModel(group, colorMap, selectedShader).apply {
            group.model = this
            init()
            updateVoxels()
        }
    }

    /** Updates high-level properties like position, size, visibility.
     * Doesn't update the voxel meshes. */
    fun updateVisible(groups: Collection<SceneVoxelGroup>) {
        groups.forEach { group ->
            getModel(group).apply {
                if (!visible) visible = true
                updatePosition()
            }
        }
        val visibleSet = groups.toSet()
        for ((group, model) in models) {
            if (group !in visibleSet) model.visible = false
        }
    }

    /** Re-creates voxel mesh. */
    fun updateVoxels(groups: Collection<SceneVoxelGroup>) {
        groups.forEach {
            getModel(it).updateVoxels()
        }
    }

    override fun runFrame(viewProj: Matrix4f) {
        for ((_, model) in models) {
            model.runFrame(viewProj)
        }
        // Render selection texture
        selectionFbo.setViewport(vp)
        selectionFbo.render {
            glClearColor(0f, 0f, 0f, 1f)
            glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
            for ((_, model) in models) {
                model.pickModel.runFrame(viewProj)
            }
        }
    }

}