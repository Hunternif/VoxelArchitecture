package hunternif.voxarch.builder

import hunternif.voxarch.plan.*

// Convenience base classes extending Builder with concrete Node types

abstract class ANodeBuilder: Builder<Node>(Node::class.java)
abstract class ARoomBuilder: Builder<Room>(Room::class.java)
abstract class APolyRoomBuilder: Builder<PolyRoom>(PolyRoom::class.java)
abstract class AFloorBuilder: Builder<Floor>(Floor::class.java)
abstract class AWallBuilder: Builder<Wall>(Wall::class.java)
abstract class APathBuilder: Builder<Path>(Path::class.java)
abstract class APropBuilder: Builder<Prop>(Prop::class.java)
abstract class AGateBuilder: Builder<Gate>(Gate::class.java)
abstract class AHatchBuilder: Builder<Hatch>(Hatch::class.java)