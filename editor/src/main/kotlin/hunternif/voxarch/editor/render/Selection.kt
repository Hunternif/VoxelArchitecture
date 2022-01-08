package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.util.AABBi
import org.joml.AABBf
import org.joml.Vector3i

class Selection(
    start: Vector3i,
    end: Vector3i,
) : AABBi() {
    var start: Vector3i = start
        set(value) {
            field = value
            correctBounds()
        }
    var end: Vector3i = end
        set(value) {
            field = value
            correctBounds()
        }

    init {
        correctBounds()
    }

    override fun correctBounds(): AABBf {
        minX = start.x.toFloat()
        minY = start.y.toFloat()
        minZ = start.z.toFloat()
        maxX = end.x.toFloat()
        maxY = end.y.toFloat()
        maxZ = end.z.toFloat()
        return super.correctBounds()
    }
}