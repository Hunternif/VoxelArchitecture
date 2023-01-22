package hunternif.voxarch.editor.util

import imgui.ImGui
import org.joml.Vector3f
import org.joml.Vector4f
import java.nio.ByteBuffer
import kotlin.math.min
import kotlin.math.round

/** Borrowed from OpenRNDR */
data class ColorRGBa(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float = 1f,
) {
    fun toVector3f() = Vector3f(r, g, b)
    fun toVector4f() = Vector4f(r, g, b, a)

    val hex: Int = calculateHexRGB()

    private fun calculateHexRGB(): Int {
        val ri = round(255f * r).toInt()
        val gi = round(255f * g).toInt()
        val bi = round(255f * b).toInt()
        val rbit = (ri shl 16) and 0xff0000
        val gbit = (gi shl 8) and 0x00ff00
        val bbit = bi and 0x0000ff
        return rbit or gbit or bbit
    }

    /** Add together all components of the 2 colors */
    fun add(c: ColorRGBa) = ColorRGBa(
        min(1f, r + c.r),
        min(1f, g + c.g),
        min(1f, b + c.b),
        min(1f, a + c.a),
    )

    /** Alpha-blend the 2 colors. The given color [c] is on top. */
    fun blend(c: ColorRGBa): ColorRGBa {
        if (a == 0f) return c
        if (c.a == 0f) return this
        val alpha = min(1f, a * (1f - c.a) + c.a)
        if (alpha == 0f) return ColorRGBa(0f, 0f, 0f, 0f)
        return ColorRGBa(
            min(1f, (r * a * (1f - c.a) + c.r * c.a) / alpha),
            min(1f, (g * a * (1f - c.a) + c.g * c.a) / alpha),
            min(1f, (b * a * (1f - c.a) + c.b * c.a) / alpha),
            alpha,
        )
    }

    override fun toString(): String {
        return String.format("0x%06X %.0f%%", hex, a * 100)
    }

    companion object {
        fun fromHex(hex: Int, alpha: Float = 1f): ColorRGBa {
            val r = hex and (0xff0000) shr 16
            val g = hex and (0x00ff00) shr 8
            val b = hex and (0x0000ff)
            return ColorRGBa(r / 255f, g / 255f, b / 255f, alpha)
        }

        fun fromRGBBytes(buffer: ByteBuffer): ColorRGBa {
            val r = buffer.get().toInt() and 0xff
            val g = buffer.get().toInt() and 0xff
            val b = buffer.get().toInt() and 0xff
            return ColorRGBa(r / 255f, g / 255f, b / 255f)
        }
    }
}

fun pushStyleColor(imGuiCol: Int, color: ColorRGBa) {
    color.run { ImGui.pushStyleColor(imGuiCol, r, g, b, a) }
}