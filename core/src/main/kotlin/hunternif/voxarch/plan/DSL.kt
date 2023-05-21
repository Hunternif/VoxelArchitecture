package hunternif.voxarch.plan

import hunternif.voxarch.util.SnapOrigin
import hunternif.voxarch.util.snapStart
import hunternif.voxarch.vector.Vec3

/** Adds a child [Room], measured from corner */
inline fun Node.node(
    origin: Vec3,
    size: Vec3 = Vec3.ZERO,
    crossinline action: Node.() -> Unit = {}
): Node = Node(origin).also {
    it.size = size
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Room], measured from corner */
inline fun Node.room(
    start: Vec3,
    end: Vec3,
    crossinline action: Room.() -> Unit = {}
): Room = Room(start, end - start).also {
    it.snapStart(SnapOrigin.CORNER)
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Room], measured from center at floor level */
inline fun Node.centeredRoom(
    center: Vec3,
    size: Vec3,
    crossinline action: Room.() -> Unit = {}
): Room = Room(center, size).also {
    it.snapStart(SnapOrigin.FLOOR_CENTER)
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [PolyRoom], measured from corner */
inline fun Node.polyRoom(
    start: Vec3,
    end: Vec3,
    crossinline action: PolyRoom.() -> Unit = {}
): PolyRoom = PolyRoom(start, end - start).also {
    it.snapStart(SnapOrigin.CORNER)
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [PolyRoom], measured from center at floor level */
inline fun Node.centeredPolyRoom(
    center: Vec3,
    size: Vec3,
    crossinline action: PolyRoom.() -> Unit = {}
): PolyRoom = PolyRoom(center, size).also {
    it.snapStart(SnapOrigin.FLOOR_CENTER)
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Floor] */
inline fun Node.floor(
    height: Double = 0.0,
    crossinline action: Floor.() -> Unit = {}
): Floor = Floor(height).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a room-bound [Floor] at ceiling level, matching room size. */
inline fun Node.ceiling(
    crossinline action: Floor.() -> Unit = {}
): Floor = Floor(height).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a path that follows the room's walls at [y] level. */
inline fun Room.perimeter(
    y: Double = 0.0,
    crossinline action: Path.() -> Unit = {}
): Path = Path(Vec3(0.0, y, 0.0)).also { path ->
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
): Wall = Wall(start, end).also {
    this.addChild(it)
    action.invoke(it)
}

/** Adds a child [Path] that follows this wall at [y] level. */
inline fun Wall.path(
    y: Double = 0.0,
    crossinline action: Path.() -> Unit = {}
): Path = Path(Vec3(0.0, y, 0.0)).also {
    it.addPoint(Vec3.ZERO)
    // move only along x because Wall overrides rotationY
    it.addPoint(Vec3(depth, 0.0, 0.0))
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