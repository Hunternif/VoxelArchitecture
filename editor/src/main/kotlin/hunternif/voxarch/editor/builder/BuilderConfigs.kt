package hunternif.voxarch.editor.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.editor.render.AtlasEntry
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.render.TextureAtlas
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.resourcePath
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IVoxel

fun BuilderConfig.setDefaultBuilders() {
    setDefault(SimpleFloorBuilder(MAT_FLOOR))
    setDefault(SimpleWallBuilder(MAT_WALL))
    setDefault(RoomBuilder())
    setDefault(SimpleGateBuilder())
    setDefault(SimpleHatchBuilder())
    setDefault<Node>(Builder())
}


// ============================ Solid color voxels ============================

class SolidColorBlock(val color: Int) : BlockData(color.toString(16)) {
    override fun clone() = SolidColorBlock(color)
}

private val solidStoneBrick = SolidColorBlock(0x797979)
private val solidStone = SolidColorBlock(0x6C6C6C)
private val solidCobblestone = SolidColorBlock(0x626162)
private val solidDarkOak = SolidColorBlock(0x3F2813)
private val solidTorch = SolidColorBlock(0xFCE6A3)

fun mapVoxelToSolidColor(voxel: IVoxel): ColorRGBa = when (voxel) {
    is SolidColorBlock -> ColorRGBa.fromHex(voxel.color)
    is VoxColor -> ColorRGBa.fromHex(voxel.color)
    else -> ColorRGBa.fromHex(0x999999)
}

fun MaterialConfig.setSolidColorMaterials() {
    set(MAT_FLOOR) { solidStone }
    set(MAT_WALL) { arrayOf(solidStone, solidCobblestone).random() }
    set(MAT_WALL_DECORATION) { solidStoneBrick }
    set(MAT_ROOF) { solidDarkOak }
    set(MAT_TORCH) { solidTorch }
    set(MAT_POST) { solidDarkOak }
}


// ============================= Textured voxels ==============================

class TexturedBlock(key: String, val atlasEntry: AtlasEntry) : BlockData(key) {
    override fun clone() = TexturedBlock(key, atlasEntry)
}

private val texStone by lazy { loadTexturedBlock("stone") }
private val texStoneBrick by lazy { loadTexturedBlock("stone_bricks") }
private val texCobblestone by lazy { loadTexturedBlock("cobblestone") }
private val texDarkOak by lazy { loadTexturedBlock("dark_oak_planks") }

fun MaterialConfig.setMinecraftMaterials() {
    set(MAT_FLOOR) { texStone }
    set(MAT_WALL) { arrayOf(texStone, texCobblestone).random() }
    set(MAT_WALL_DECORATION) { texStoneBrick }
    set(MAT_ROOF) { texDarkOak }
//    set(MAT_TORCH) { solidTorch }
    set(MAT_POST) { texDarkOak }
}

val minecraftTexAtlas by lazy {
    TextureAtlas(64, 64, 2).apply { init() }
}

private fun loadTexturedBlock(name: String): TexturedBlock {
    val path = minecraftTexAtlas.resourcePath("textures/minecraft/block/$name.png")
    val texture = Texture(path.toString())
    val entry = minecraftTexAtlas.add(texture)
    return TexturedBlock(name, entry)
}

