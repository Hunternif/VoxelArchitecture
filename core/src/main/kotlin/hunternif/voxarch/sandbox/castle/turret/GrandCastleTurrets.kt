package hunternif.voxarch.sandbox.castle.turret

import hunternif.voxarch.util.clamp
import hunternif.voxarch.util.next
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.util.roundToEven
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

/** How many sub-elements, values 0.5 - 10*/
private const val dense: Double = 6.0

/** How tall and narrow the turrets, values 0.05 - 1*/
private const val tall: Double = 0.5

private const val minWidth = 2.0
private const val maxWidth = 12.0
private const val widthRatio = 0.2 + (dense/10)*0.6
private const val heightRatio = 0.4 + (1 - tall)*0.4

private val maxDepth = ceil(dense/3).toInt()

fun Turret.addGrandCastleTurretsRecursive(seed: Long) {
    val depth = level + 1
    if (depth > maxDepth) return

    // Assuming the current turret can be non-symmetrical,
    // use its smallest dimension as 'width':
    val parentWidth = min(this.size.x, this.size.z)

    val denseTurrets = dense + Random(seed+123).nextDouble(0.0, 2.0)
    val turretCount = ceil(denseTurrets/3).toInt() - depth + 1

    for (t in 1 .. turretCount) {
        val bodyShape = Random(seed + depth)
            .next(*BodyShape.values())
        val roofShape = Random(seed + depth)
            .next(*RoofShape.values())

        // Don't rotate the turret itself (square looks bad at an angle),
        // but take into account orientation of the parent.
        val angle = Random(seed + t*10 + depth)
            .nextInt(-200, 201) + this.turretAngle

        val height = round(this.size.y * heightRatio)
        val width = (parentWidth * widthRatio)
            .clamp(minWidth, maxWidth)
            .roundToEven() // even sizes are better for symmetry

        val yOffset = ceil(
            Random(seed + t + depth)
                .nextDouble(min(width, this.size.y-1), this.size.y)
        )

        val origin = Vec3.UNIT_X.rotateY(angle).also {
            it.y = yOffset
            it.x *= round(this.size.x * 0.55)
            it.z *= round(this.size.z * 0.55)
        }

        turret(
            origin = origin,
            size = Vec3(width, height, width),
            roofShape = roofShape,
            bodyShape = bodyShape,
            bottomShape = BottomShape.TAPERED,
            positionType = TurretPosition.WALL,
            style = this.style,
            angle = angle,
            level = this.level + 1
        ) {
            addGrandCastleTurretsRecursive(seed)
        }
    }
}