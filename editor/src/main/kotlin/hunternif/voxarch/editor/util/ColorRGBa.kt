package hunternif.voxarch.editor.util

import imgui.ImGui
import org.joml.Vector3f
import org.joml.Vector4f
import java.nio.ByteBuffer
import kotlin.math.min
import kotlin.math.round

/** Borrowed from OpenRNDR */
data class ColorRGBa(
    var r: Float,
    var g: Float,
    var b: Float,
    var a: Float = 1f,
) {
    fun toVector3f() = Vector3f(r, g, b)
    fun toVector4f() = Vector4f(r, g, b, a)

    var hex: Int = calculateHexRGB()
        private set
    var hexABGR: Int = calculateHexABGR()
        private set

    private fun calculateHexRGB(): Int {
        val ri = round(255f * r).toInt()
        val gi = round(255f * g).toInt()
        val bi = round(255f * b).toInt()
        val rbit = (ri shl 16) and 0xff0000
        val gbit = (gi shl 8) and 0x00ff00
        val bbit = bi and 0x0000ff
        return rbit or gbit or bbit
    }

    /** Add together all components of the 2 colors, returns new instance. */
    fun add(c: ColorRGBa) = ColorRGBa(
        min(1f, r + c.r),
        min(1f, g + c.g),
        min(1f, b + c.b),
        min(1f, a + c.a),
    )

    /** Modifies this color */
    fun set(c: ColorRGBa) = apply {
        r = c.r
        g = c.g
        b = c.b
        a = c.a
        hex = calculateHexRGB()
        hexABGR = calculateHexABGR()
    }

    /** Alpha-blend the 2 colors. The given color [c] is on top. Returns new instance. */
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

    private fun calculateHexABGR(): Int {
        val ri = round(255f * r).toInt()
        val gi = round(255f * g).toInt()
        val bi = round(255f * b).toInt()
        val ai = round(255f * a).toInt()
        val abit = (ai shl 24) and (0xff shl 24)
        val bbit = (bi shl 16) and 0xff0000
        val gbit = (gi shl 8) and 0x00ff00
        val rbit = ri and 0x0000ff
        return abit or rbit or gbit or bbit
    }

    override fun toString(): String {
        return String.format("0x%06X %.0f%%", hex, a * 100)
    }

    /** [array] must fit 4 items */
    fun writeToFloatArray(array: FloatArray) {
        array[0] = r
        array[1] = g
        array[2] = b
        array[3] = a
    }

    /** [array] must fit 4 items */
    fun readFromFloatArray(array: FloatArray) {
        r = array[0]
        g = array[1]
        b = array[2]
        a = array[3]
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

        /** [array] must fit 4 items */
        fun readFromFloatArray(array: FloatArray): ColorRGBa {
            val r = array[0]
            val g = array[1]
            val b = array[2]
            val a = array[3]
            return ColorRGBa(r, g, b, a)
        }
    }
}

fun pushStyleColor(imGuiCol: Int, color: ColorRGBa) {
    color.run { ImGui.pushStyleColor(imGuiCol, r, g, b, a) }
}