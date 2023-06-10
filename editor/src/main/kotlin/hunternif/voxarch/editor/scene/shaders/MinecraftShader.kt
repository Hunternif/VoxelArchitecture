package hunternif.voxarch.editor.scene.shaders

import hunternif.voxarch.editor.builder.minecraftTexAtlas
import hunternif.voxarch.editor.util.aoTextureAtlas
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

            uploadFloat("depthOffset", depthOffset)

            renderMode = VoxelRenderMode.TEXTURED
            uploadInt("uRenderMode", renderMode.id)

            uploadTexture("uBlockTexture", 0)
            uploadTexture("uAOTexture", 1)
            texture = minecraftTexAtlas.sheet
            aoTexture = aoTextureAtlas.sheet
        }
    }
}