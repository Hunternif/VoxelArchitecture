package hunternif.voxarch.editor.render

import org.joml.Vector2f
import kotlin.math.sqrt

/**
 * Represents a viewport on the screen, where (0, 0) is the top left corner.
 */
data class Viewport(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
) {
    /** Distance from the left side of the window to the LEFT side of this viewport */
    inline val left get() = x
    /** Distance from the left side of the window to the RIGHT side of this viewport */
    inline val right get() = x + width
    /** Distance from the top of the window to the TOP of this viewport */
    inline val top get() = y
    /** Distance from the top of the window to the BOTTOM of this viewport */
    inline val bottom get() = y + height

    /** Returns offset in GLFW window coordinates */
    val windowOffset: Vector2f = Vector2f()
        get() = field.set(x.toFloat(), -y.toFloat())

    constructor(vp: Viewport) : this(vp.x, vp.y, vp.width, vp.height)

    private val array = intArrayOf(0, 0, width, height)

    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun set(x: Number, y: Number, width: Number, height: Number) =
        set(x.toInt(), y.toInt(), width.toInt(), height.toInt())

    fun set(vp: Viewport) = set(vp.x, vp.y, vp.width, vp.height)

    /** Sets only width & height, i.e. coordinates are (0, 0) */
    fun getSizeArray(): IntArray {
        array[0] = 0
        array[1] = 0
        array[2] = width
        array[3] = height
        return array
    }

    fun contains(posX: Int, posY: Int): Boolean =
        posX >= x && posX <= x + width &&
            posY >= y && posY <= y + width

    fun contains(posX: Float, posY: Float): Boolean =
        posX >= x && posX <= x + width &&
            posY >= y && posY <= y + height

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

    override fun toString() = "Viewport($x, $y, $width, $height)"
}