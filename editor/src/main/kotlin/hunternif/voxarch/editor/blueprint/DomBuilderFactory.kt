package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.plan.Room

/**
 * Creates a new DomBuilder for a Blueprint node.
 */
typealias DomBuilderFactory = () -> DomBuilder

val domBuilderFactoryByName: Map<String, DomBuilderFactory> = mapOf(
    "Turret" to { DomPolyRoomWithTurretBuilder() },
    "Turret Decor" to { DomTurretDecor() },
    "Room" to { DomNodeBuilder { Room() } },
    "Extend" to { DomExtend() },
)
