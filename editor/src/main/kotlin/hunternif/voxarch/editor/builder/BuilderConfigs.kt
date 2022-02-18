package hunternif.voxarch.editor.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.sandbox.castle.*
import hunternif.voxarch.storage.BlockData

fun BuilderConfig.setDefaultBuilders() {
    setDefault(SimpleFloorBuilder(MAT_FLOOR))
    setDefault(SimpleWallBuilder(MAT_WALL))
    setDefault(RoomBuilder())
    setDefault(SimpleGateBuilder())
    setDefault(SimpleHatchBuilder())
    setDefault<Node>(Builder())
}

class SolidColorBlock(color: Int) : BlockData(color.toString(16))
private val stoneBrick = SolidColorBlock(0x797979)
private val stone = SolidColorBlock(0x6C6C6C)
private val cobblestone = SolidColorBlock(0x626162)
private val darkOak = SolidColorBlock(0x3F2813)
private val torch = SolidColorBlock(0xFCE6A3)

fun MaterialConfig.setSolidColorMaterials() {
    set(MAT_FLOOR) { stone }
    set(MAT_WALL) { arrayOf(stone, cobblestone).random() }
    set(MAT_WALL_DECORATION) { stoneBrick }
    set(MAT_ROOF) { darkOak }
    set(MAT_TORCH) { torch }
    set(MAT_POST) { darkOak }
}