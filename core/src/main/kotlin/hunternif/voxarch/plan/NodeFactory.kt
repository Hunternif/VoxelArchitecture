package hunternif.voxarch.plan

import hunternif.voxarch.sandbox.castle.turret.Turret
import hunternif.voxarch.vector.Vec2
import hunternif.voxarch.vector.Vec3
import kotlin.math.max

/** Creates instances of new Nodes and assigns them unique ids. */
open class NodeFactory {
    fun newNode() = register(Node(Vec3.ZERO))
    fun newNode(origin: Vec3) = register(Node(origin))

    fun newRoom() = register(Room(Vec3.ZERO, Vec3.ZERO))
    fun newRoom(origin: Vec3, size: Vec3) = register(Room(origin, size))
    fun newRoom(origin: Vec3, size: Vec3, rotationY: Double) =
        register(Room(origin, size).also { it.rotationY = rotationY })

    fun newPolygonRoom() = register(PolygonRoom(Vec3.ZERO, Vec3.ZERO))
    fun newPolygonRoom(origin: Vec3, size: Vec3) = register(PolygonRoom(origin, size))

    fun newTurret() = register(Turret(Vec3.ZERO, Vec3.ZERO))
    fun newTurret(origin: Vec3, size: Vec3) = register(Turret(origin, size))

    fun newProp(type: String) = register(Prop(Vec3.ZERO, type))
    fun newProp(origin: Vec3, type: String) = register(Prop(origin, type))

    fun newPath() = register(Path(Vec3.ZERO))
    fun newPath(origin: Vec3) = register(Path(origin))
    fun newPath(origin: Vec3, vararg points: Vec3) =
        register(Path(origin).also { path ->
            points.forEach { path.addPoint(it) }
        })

    fun newGate() = register(Gate(Vec3.ZERO, Vec2.ZERO))
    fun newGate(origin: Vec3, size: Vec2, rotationY: Double) =
        register(Gate(origin, size, rotationY))

    fun newStructure() = register(Structure(Vec3.ZERO))
    fun newStructure(origin: Vec3) = register(Structure(origin))

    fun newCorridor() = register(Corridor(Vec3.ZERO, Vec2.ZERO))
    fun newCorridor(origin: Vec3, sectionSize: Vec2) = register(
        Corridor(origin, sectionSize))

    fun newHatch() = register(Hatch(Vec3.ZERO, Vec2.ZERO))
    fun newHatch(origin: Vec3, size: Vec2, rotationY: Double) =
        register(Hatch(origin, size, rotationY))

    fun newFloor() = register(Floor(0.0))
    fun newFloor(height: Double) = register(Floor(height))

    fun newWall() = register(Wall(Vec3.ZERO, Vec3.ZERO))
    fun newWall(start: Vec3, end: Vec3) = register(Wall(start, end))
    fun newWall(p1: Vec2, p2: Vec2, height: Double, transparent: Boolean) =
        register(Wall(
            Vec3(p1.x, 0.0, p1.y),
            Vec3(p2.x, height, p2.y),
            transparent
        ))


    //=========================== REGISTRY CODE =============================

    private var lastNodeIndex = -1
    private val map = mutableMapOf<Int, Node>()

    /** Creates a new id and sets it on this node, returns node. */
    private fun <T: Node> register(node: T): T {
        lastNodeIndex++
        map[lastNodeIndex] = node
        node.id = lastNodeIndex
        node.factory = this
        return node
    }

    /**
     * Register the given [node] with an existing [id], e.g. when loading from
     * a file.
     * If another node already exists with the given [id], then the given [node]
     * will have its id generated.
     * Returns node.
     */
    fun <T: Node> load(id: Int, node: T): T {
        if (map.containsKey(id)) {
            register(node)
        } else {
            map[id] = node
            node.id = id
            node.factory = this
            lastNodeIndex = max(id, lastNodeIndex)
        }
        return node
    }

    fun clear() {
        lastNodeIndex = -1
        map.clear()
    }

    companion object {
        val default = NodeFactory()
    }
}