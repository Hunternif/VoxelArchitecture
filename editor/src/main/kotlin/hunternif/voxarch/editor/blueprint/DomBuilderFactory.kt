package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.plan.*

/**
 * Creates a new DomBuilder for a Blueprint node.
 */
typealias DomBuilderFactory = () -> DomBuilder

val domBuilderFactoryByName: Map<String, DomBuilderFactory> = mapOf(
    "Node" to { DomNodeBuilder { Node() } },
    "Room" to { DomNodeBuilder { Room() } },
    "PolyRoom" to { DomPolyRoomBuilder { PolyRoom() } },
    "Floor" to { DomNodeBuilder { Floor() } },
    "Wall" to { DomNodeBuilder { Wall() } },
    "Window" to { DomNodeBuilder { Window() } },
    "Column" to { DomNodeBuilder { Column() } },
    "Arched Bridge" to { DomNodeBuilder { Wall() }.addStyle(BLD_ARCHED_BRIDGE) },
    "Arched Window" to { DomNodeBuilder { Window() }.addStyle(BLD_ARCHED_WINDOW) },
    "Turret" to { DomPolyRoomWithTurretBuilder() },
    "Turret Decor" to { DomTurretDecor() },
    "Turret Roof Decor" to { DomTurretRoofDecor() },
    "Turret Bottom Decor" to { DomTurretBottomDecor() },
    "Extend" to { DomExtend() },
    "All Walls" to { DomPolySegmentBuilder() },
    "Four Walls" to { DomFourWallsBuilder() },
    "Select Walls" to { DomSelectWalls() },
    "Random Wall" to { DomRandomSegmentBuilder() },
    "All Corners" to { DomLogicPolyCornerBuilder() },
    "Four Corners" to { DomLogicFourCornerBuilder() },
    "Subdivide" to { DomSubdivide() },
    "Repeat" to { DomRepeat() },
    "Blueprint" to { DomRunBlueprint() },
    "Random" to { DomRandom() },
)

/**
 * Creates a new Node instance.
 */
typealias NodeFactory = () -> Node

val nodeFactoryByName: Map<String, NodeFactory> = mapOf(
    "Node" to { Node() },
    "Room" to { Room() },
    "PolyRoom" to { PolyRoom() },
    "Floor" to { Floor() },
    "Wall" to { Wall() },
    "Window" to { Window() },
    "Column" to { Column() },
)