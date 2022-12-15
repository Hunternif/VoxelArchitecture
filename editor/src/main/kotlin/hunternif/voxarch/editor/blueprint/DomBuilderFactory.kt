package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.builder.DomPolyRoomWithTurretBuilder
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.plan.Room

/**
 * When a Blueprint is executed, it constructs a new DOM tree given a root Node.
 * Each DOM builder in that tree must be a new instance, hence the factory for
 * creating these instances.
 * Reasons why we need new instances:
 * - reference to root
 * - stylesheet is copied from root
 * - seed is modified from the base
 * TODO: fix these reasons and make DOM builders stateless.
 */
typealias DomBuilderFactory = (parent: DomBuilder) -> DomBuilder

val domBuilderFactoryByName: Map<String, DomBuilderFactory> = mapOf(
    "Turret" to { DomPolyRoomWithTurretBuilder() },
    "Turret Decor" to { DomTurretDecor() },
    "Room" to { DomNodeBuilder { Room() } }
)
