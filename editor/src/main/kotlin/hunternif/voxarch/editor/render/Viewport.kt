package hunternif.voxarch.editor.render

import kotlin.math.sqrt

data class Viewport(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
) {
    inline val left get() = x
    inline val right get() = x + width
    inline val bottom get() = y + height
    inline val top get() = y

    private val array = intArrayOf(x, y, width, height)

    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun set(x: Number, y: Number, width: Number, height: Number) =
        set(x.toInt(), y.toInt(), width.toInt(), height.toInt())

    fun set(vp: Viewport) = set(vp.x, vp.y, vp.width, vp.height)

    fun toArray(): IntArray {
        array[0] = x
        array[1] = y
        array[2] = width
        array[3] = height
        return array
    }

    fun contains(posX: Int, posY: Int): Boolean =
        posX >= x && posX <= x + width &&
            posY >= y && posY <= y + width

    fun contains(posX: Float, posY: Float): Boolean =
        posX >= x && posX <= x + width &&
            posY >= y && posY <= y + width

    /** Distance to the closest edge of the viewport, if ([x], [y]) is outside.
     * If ([x], [y]) is inside, returns 0. */
    fun distanceToPoint(x: Float, y: Float): Float {
        val dx: Float = when {
            x < this.x -> this.x.toFloat() - x
            x > this.x + width -> x - this.x - width
            else -> 0f
        }
        val dy: Float = when {
            y < this.y -> this.y.toFloat() - y
            y > this.y + height -> x - this.y - height
            else -> 0f
        }
        return sqrt(dx*dx + dy*dy)
    }
}