package hunternif.voxarch.plan

import hunternif.voxarch.vector.Matrix4
import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.vector.Vec4

/**
 * ```
 * Y
 *  +--------------------> X (East)
 *  |           inside
 *  |  origin == gate ==
 *  |           outside
 *  V
 *  Z
 * ```
 * A horizontal gate between two [Room]s.
 * The gate's generator should clear the passage and may add decorations.
 * @param origin the lower-left point, will be copied
 * @param size X=width, Y=height
 */
open class Gate(
    origin: Vec3,
    var size: Vec2
) : Node(origin) {

    /** Center at floor level, relative to parent's origin. For legacy tests. */
    val center: Vec3
        get() = origin.add(
            Matrix4.rotationY(rotationY)
                .multiplyLocal(
                    Vec4(size.x/2, 0.0, 0.0, 1.0)
                ).let { Vec3.from(it) }
        )

    /**
     * legacy constructor
     * @param origin center at floor level
     */
    constructor(
        parent: Node?,
        room1: Node,
        room2: Node,
        origin: Vec3,
        size: Vec2,
        rotationY: Double
    ): this(
        // move origin from center to corner
        origin.add(
            Matrix4.rotationY(rotationY)
                .multiplyLocal(
                    Vec4(-size.x/2, 0.0, 0.0, 1.0)
                ).let { Vec3.from(it) }
        ),
        size
    ) {
        this.parent = parent
        this.rotationY = rotationY
    }
}
