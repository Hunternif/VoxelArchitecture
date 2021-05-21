package hunternif.voxarch.sandbox.castle.turret

import hunternif.voxarch.util.rotateY
import hunternif.voxarch.vector.Vec3

private const val minWidth = 2

fun Turret.add4TurretsRecursive() {
    if (this.size.x < minWidth) return

    for (angle in 0..270 step 90) {
        val size = this.size.multiply(0.333)
        val origin = Vec3.UNIT_X.rotateY(angle).also {
            it.y = this.size.y - size.y / 2
            it.x *= (this.size.x / 2 + 1)
            it.z *= (this.size.z / 2 + 1)
        }
        turret(
            origin = origin,
            size = size,
            roofShape = this.roofShape,
            bodyShape = this.bodyShape,
            bottomShape = BottomShape.FOUNDATION,
            positionType = TurretPosition.WALL,
            style = this.style,
            level = this.level + 1
        ) {
            add4TurretsRecursive()
        }
    }
}