package hunternif.voxarch.sandbox.castle.turret

import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

class Turret(
    /** maps to the center of the floor */
    origin: Vec3,
    size: Vec3,
    val roofShape: RoofShape = RoofShape.FLAT_BORDERED,
    val bodyShape: BodyShape = BodyShape.SQUARE,
    val bottomShape: BottomShape = BottomShape.FLAT,
    val positionType: TurretPosition = TurretPosition.NONE,
    val style: TowerStyle = TowerStyle(),

    /** Angle vs parent turret. Usually facing away from the center. */
    val turretAngle: Double = 0.0,

    /** Level in a hierarchy of nested turrets.
     * Usually equal to recursion depth, but not always. */
    val level: Int = 0
) : Room(origin, size)

enum class RoofShape {
    /** Flat top surrounded by a border. */
    FLAT_BORDERED,
    /** Pyramid/cone roof with no border. */
    SPIRE,
    /** Pyramid/cone roof surrounded by a border. */
    SPIRE_BORDERED,
    /** Spherical dome. */
    //DOME
}
enum class BodyShape {
    /** Square or rectangle. */
    SQUARE,
    /** Round or oval. */
    ROUND
}
enum class BottomShape {
    /** Turrets sits directly on top of parent turret. */
    FLAT,
    /** Stretches down to the ground. */
    FOUNDATION,
    /** Tapers to a point. Could be partially hidden in parent turret's wall. */
    TAPERED
}
enum class TurretPosition {
    /** Sits on the edge of the wall of parent turret. */
    WALL,
    /** Sits away from parent turret with a bridge to parent's wall. */
    WALL_BRIDGE,
    /** Sits on the corner of 2 walls of _rectangular_ parent turret. */
    CORNER,
    /** Sits away from parent turret with a bridge to parent's corner. */
    CORNER_BRIDGE,
    /** Sits directly on the roof of parent turret. */
    TOP,
    /** No relation to parent turret. */
    NONE
}

/**
 * Parameters that affect geometry, that should apply to all child turrets.
 */
data class TowerStyle(
    /** Offset for borders and spires in all child turrets. */
    val roofOffset: Int = 1,
    /** Y/X ratio of spires for all child turrets. */
    val spireRatio: Double = 1.5,
    /** Y/X ratio of tapered bottoms of turrets. */
    val turretTaperRatio: Double = 0.75
)
