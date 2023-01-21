package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.builder.minecraftTexAtlas
import hunternif.voxarch.editor.util.resourcePath
import org.joml.Matrix4f

class MinecraftShader: VoxelShader() {
    override fun init() {
        super.init(
            resourcePath("shaders/base-voxel.vert.glsl"),
            resourcePath("shaders/minecraft.frag.glsl")
        ) {
            uploadFloat("uDarkenTop", 0.05f)
            uploadFloat("uDarkenZ", 0.25f)
            uploadFloat("uDarkenX", 0.43f)
            uploadFloat("uDarkenBottom", 0.54f)

            uploadMat4f("uModel", Matrix4f())

            updateRenderMode(VoxelRenderMode.TEXTURED)

            uploadTexture("uTexSampler", 0)
            texture = minecraftTexAtlas.sheet
        }
    }
}