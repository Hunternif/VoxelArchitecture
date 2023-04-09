package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

class Column(origin: Vec3, size: Vec3) : PolyRoom(origin, size) {
    constructor() : this(Vec3.ZERO, Vec3.ZERO)
}