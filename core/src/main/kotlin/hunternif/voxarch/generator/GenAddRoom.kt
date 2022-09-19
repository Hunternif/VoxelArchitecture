package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.room
import hunternif.voxarch.plan.Node

@PublicGenerator("Add room")
class GenAddRoom(vararg styleClass: String) : IGenerator {
    private val styles = styleClass

    @GeneratorSlot("room")
    var nextGen: IGenerator? = null

    override fun generate(parent: DomBuilder<Node?>) {
        parent.room(*styles) {
            nextGen?.let { generators.add(it) }
        }
    }
}