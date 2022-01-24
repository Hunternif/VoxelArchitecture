package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/** Adds a child [Room], measured from corner */
inline fun Node.room(
    start: Vec3,
    end: Vec3,
    crossinline action: Room.() -> Unit = {}
): Room = factory.newRoom(start, end - start).also {
    it.start = Vec3(0, 0, 0)
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Room], measured from center at floor level */
inline fun Node.centeredRoom(
    center: Vec3,
    size: Vec3,
    crossinline action: Room.() -> Unit = {}
): Room = factory.newRoom(center, size).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [PolygonRoom], measured from corner */
inline fun Node.polygonRoom(
    start: Vec3,
    end: Vec3,
    crossinline action: PolygonRoom.() -> Unit = {}
): PolygonRoom = centeredPolygonRoom(
    Vec3((start.x + end.x)/2, start.y, (start.z + end.z)/2),
    end.subtract(start),
    action
)

/** Adds a child [PolygonRoom], measured from center at floor level */
inline fun Node.centeredPolygonRoom(
    center: Vec3,
    size: Vec3,
    crossinline action: PolygonRoom.() -> Unit = {}
): PolygonRoom = factory.newPolygonRoom(center, size).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Floor] */
inline fun Room.floor(
    height: Double = 0.0,
    crossinline action: Floor.() -> Unit = {}
): Floor = factory.newFloor(height).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a room-bound [Floor] at ceiling level, matching room size. */
inline fun Room.ceiling(
    crossinline action: Floor.() -> Unit = {}
): Floor = factory.newFloor(height).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a path that follows the room's walls at [y] level. */
inline fun Room.perimeter(
    y: Double = 0.0,
    crossinline action: Path.() -> Unit = {}
): Path = factory.newPath(Vec3(0.0, y, 0.0)).also { path ->
    this.walls.forEach { wall ->
        path.addPoint(Vec3(wall.p1.x, 0.0, wall.p1.y))
    }
    path.loopToStart()
    this.addChild(path)
    action.invoke(path)
}

/** Adds a child [Wall] */
inline fun Node.wall(
    start: Vec3,
    end: Vec3,
    crossinline action: Wall.() -> Unit = {}
): Wall = factory.newWall(start, end).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Path] that follows this wall at [y] level. */
inline fun Wall.path(
    y: Double = 0.0,
    crossinline action: Path.() -> Unit = {}
): Path = factory.newPath(Vec3(0.0, y, 0.0)).also {
    it.addPoint(Vec3.ZERO)
    // move only along x because Wall overrides rotationY
    it.addPoint(Vec3(length, 0.0, 0.0))
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Prop] */
inline fun Node.prop(
    start: Vec3,
    type: String,
    crossinline action: Prop.() -> Unit = {}
): Prop = factory.newProp(start, type).also {
    this.addChild(it)
    action.invoke(it)
}