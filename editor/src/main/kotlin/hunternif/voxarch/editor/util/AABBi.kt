package hunternif.voxarch.editor.util

import org.joml.AABBf

open class AABBi : AABBf() {
    var minXi: Int
        get() = super.minX.toInt()
        set(value) { super.minX = value.toFloat() }
    var maxXi: Int
        get() = super.maxX.toInt()
        set(value) { super.maxX = value.toFloat() }
    var minYi: Int
        get() = super.minY.toInt()
        set(value) { super.minY = value.toFloat() }
    var maxYi: Int
        get() = super.maxY.toInt()
        set(value) { super.maxY = value.toFloat() }
    var minZi: Int
        get() = super.minZ.toInt()
        set(value) { super.minZ = value.toFloat() }
    var maxZi: Int
        get() = super.maxZ.toInt()
        set(value) { super.maxZ = value.toFloat() }

    fun testPoint(x: Int, y: Int, z: Int) = testPoint(x.toFloat(), y.toFloat(), z.toFloat())
}