package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/**
 * ```
 * Y
 *  +------------> X (East)
 *  | start --+
 *  | |       |
 *  | +---- end
 *  V
 *  Z
 * ```
 * Horizontal surface. Potentially it can be uneven, so `end` can have higher Y.
 * @param start aka `origin`, will be copied
 * @param end corner of the floor relative to _parent's origin_, with higher XZ values, will be copied
 */
open class Floor(
    start: Vec3,
    end: Vec3
): Node(start) {
    open var end: Vec3 = end.clone()
    val start inline get() = origin

    init {
        require(end.x >= start.x) { "Floor corners must be in order of increasing X" }
        require(end.y >= start.y) { "Floor corners must be in order of increasing Y" }
        require(end.z >= start.z) { "Floor corners must be in order of increasing Z" }
    }

    open val width: Double get() = end.x - start.x
    open val height: Double get() = end.y - start.y
    open val length: Double get() = end.z - start.z


    /**
     * Determines its size from the room, its own height is 0.
     * @param level Y position relative to room floor.
     */
    class RoomBound(val room: Room, val level: Double) : Floor(Vec3.ZERO, Vec3.ZERO) {
        override var origin: Vec3
            get() = Vec3(-room.width/2, level, -room.length/2)
            set(value) {}

        override var end: Vec3
            get() = Vec3(room.width/2 + 1, level, room.length/2 + 1)
            set(value) {}

        override val width: Double get() = room.width
        override val height: Double get() = 0.0
        override val length: Double get() = room.length
    }
}