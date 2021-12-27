package hunternif.voxarch.editor.render

data class Viewport(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
) {
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

    fun contains(xpos: Int, ypos: Int): Boolean =
        xpos >= x && xpos <= x + width &&
            ypos >= y && ypos <= y + width
}