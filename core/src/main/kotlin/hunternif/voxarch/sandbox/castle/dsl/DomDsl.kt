package hunternif.voxarch.sandbox.castle.dsl

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3

/** Adds child empty [Node] */
fun DomBuilder.node(
    vararg styleClass: String,
    block: DomBuilder.() -> Unit = {}
) {
    createChild(styleClass) { Node(Vec3.ZERO) }.block()
}

/** Adds child [Room] */
fun DomBuilder.room(
    vararg styleClass: String,
    block: DomBuilder.() -> Unit = {}
) {
    createChild(styleClass) { Room(Vec3.ZERO, Vec3.ZERO) }.block()
}
