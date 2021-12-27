package hunternif.voxarch.editor.util

import org.joml.Vector3f

/** Borrowed from OpenRNDR */
data class ColorRGBa(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float = 1f,
) {
    fun toVector3f() = Vector3f(r, g, b)

    companion object {
        fun fromHex(hex: Int): ColorRGBa {
            val r = hex and (0xff0000) shr 16
            val g = hex and (0x00ff00) shr 8
            val b = hex and (0x0000ff)
            return ColorRGBa(r / 255f, g / 255f, b / 255f, 1f)
        }
    }
}