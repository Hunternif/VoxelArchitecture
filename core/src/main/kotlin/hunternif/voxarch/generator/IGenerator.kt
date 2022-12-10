package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node

/**
 * A program that adds more child nodes to the given node.
 */
interface IGenerator {
    /**
     * Add new DOM elements.
     */
    fun generate(parent: DomBuilder, parentNode: Node)
}

/** Used to locate generator classes visible in the UI. */
@Target(AnnotationTarget.CLASS)
annotation class PublicGenerator(val name: String)

/** A slot on the generator that accepts another generator. */
@Target(AnnotationTarget.PROPERTY)
annotation class GeneratorSlot(val name: String)