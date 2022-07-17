package hunternif.voxarch.editor.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.editor.util.ColorRGBa
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

class SolidColorBlock(val color: Int) : BlockData(color.toString(16)) {
    override fun clone() = SolidColorBlock(color)
}

private val stoneBrick = SolidColorBlock(0x797979)
private val stone = SolidColorBlock(0x6C6C6C)
private val cobblestone = SolidColorBlock(0x626162)
private val darkOak = SolidColorBlock(0x3F2813)
private val torch = SolidColorBlock(0xFCE6A3)

fun mapVoxelToSolidColor(voxel: IVoxel): ColorRGBa = when (voxel) {
    is SolidColorBlock -> ColorRGBa.fromHex(voxel.color)
    is VoxColor -> ColorRGBa.fromHex(voxel.color)
    else -> ColorRGBa.fromHex(0x999999)
}

fun MaterialConfig.setSolidColorMaterials() {
    set(MAT_FLOOR) { stone }
    set(MAT_WALL) { arrayOf(stone, cobblestone).random() }
    set(MAT_WALL_DECORATION) { stoneBrick }
    set(MAT_ROOF) { darkOak }
    set(MAT_TORCH) { torch }
    set(MAT_POST) { darkOak }
}