package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.plan.*

/**
 * Creates a new DomBuilder for a Blueprint node.
 */
typealias DomBuilderFactory = () -> DomBuilder

val domBuilderFactoryByName: Map<String, DomBuilderFactory> = mapOf(
    "Room" to { DomNodeBuilder { Room() } },
    "PolyRoom" to { DomPolyRoomBuilder() },
    "Floor" to { DomNodeBuilder { Floor() } },
    "Wall" to { DomNodeBuilder { Wall() } },
    "Turret" to { DomPolyRoomWithTurretBuilder() },
    "Turret Decor" to { DomTurretDecor() },
    "Extend" to { DomExtend() },
    "All Walls" to { DomPolySegmentBuilder() },
    "Four Walls" to { DomFourWallsBuilder() },
    "Random Wall" to { DomRandomSegmentBuilder() },
    "All Corners" to { DomLogicPolyCornerBuilder() },
    "Four Corners" to { DomLogicFourCornerBuilder() },
)
