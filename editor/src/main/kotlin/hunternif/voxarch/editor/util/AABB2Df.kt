package hunternif.voxarch.editor.util

import org.joml.Vector2f
import kotlin.math.max
import kotlin.math.min

/** Axis-aligned bounding box in 2D, e.g. in screen coordinates. */
class AABB2Df {
    var minX: Float = 0f
    var maxX: Float = 0f
    var minY: Float = 0f
    var maxY: Float = 0f

    fun setMin(x: Float, y: Float) {
        minX = x
        minY = y
    }
    fun setMin(min: Vector2f) {
        minX = min.x
        minY = min.y
    }
    fun setMax(x: Float, y: Float) {
        maxX = x
        maxY = y
    }
    fun setMax(max: Vector2f) {
        maxX = max.x
        maxY = max.y
    }

    fun correctBounds() {
        minX = min(minX, maxX)
        minY = min(minY, maxY)
        maxX = max(minX, maxX)
        maxY = max(minY, maxY)
    }

    fun union(x: Float, y: Float) {
        minX = min(minX, x)
        minY = min(minY, y)
        maxX = max(x, maxX)
        maxY = max(y, maxY)
    }
    fun union(p: Vector2f) {
        minX = min(minX, p.x)
        minY = min(minY, p.y)
        maxX = max(p.x, maxX)
        maxY = max(p.y, maxY)
    }

    fun testPoint(x: Float, y: Float) =
        x >= minX && y >= minY && x <= maxX && y <= maxY
}