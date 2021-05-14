package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/** Adds a child [Room], measured from corner */
inline fun Node.room(
    start: Vec3,
    end: Vec3,
    crossinline action: Room.() -> Unit = {}
): Room = centeredRoom(
    Vec3((start.x + end.x)/2, start.y, (start.z + end.z)/2),
    end.subtract(start),
    action
)

/** Adds a child [Room], measured from center at floor level */
inline fun Node.centeredRoom(
    center: Vec3,
    size: Vec3,
    crossinline action: Room.() -> Unit = {}
): Room = Room(center, size).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Floor], measured from corner */
inline fun Node.floor(
    start: Vec3,
    size: Vec3,
    crossinline action: Floor.() -> Unit = {}
): Floor = Floor(start, size).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Floor], measured from center at floor level */
inline fun Node.centeredFloor(
    center: Vec3,
    size: Vec3,
    crossinline action: Floor.() -> Unit = {}
): Floor = floor(
    center.add(-size.x/2, 0.0, -size.z/2),
    size,
    action
)

/** Adds a room-bound [Floor] at floor level, matching its size. */
inline fun Room.floor(
    crossinline action: Floor.() -> Unit = {}
): Floor = Floor(start, Vec3(width, 0.0, length)).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a room-bound [Floor] at ceiling level, matching room size. */
inline fun Room.ceiling(
    crossinline action: Floor.() -> Unit = {}
): Floor = Floor(start.addY(height),  Vec3(width, 0.0, length)).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Wall] */
inline fun Node.wall(
    start: Vec3,
    end: Vec3,
    crossinline action: Wall.() -> Unit = {}
): Wall = Wall(start, end).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Prop] */
inline fun Node.prop(
    start: Vec3,
    type: String,
    crossinline action: Prop.() -> Unit = {}
): Prop = Prop(start, type).also {
    this.addChild(it)
    action.invoke(it)
}