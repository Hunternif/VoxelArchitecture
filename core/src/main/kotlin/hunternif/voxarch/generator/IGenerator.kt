package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomLocalRoot
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/**
 * A program that adds more child nodes to the given node.
 */
interface IGenerator {
    /**
     * Add new DOM elements.
     */
    fun generate(parent: DomBuilder<Node?>)
    /**
     * Creates a new detached DOM root and generates on it.
     * Not recommended, because this will ignore styles or nested generators.
     */
    fun generateFinal(
        parent: Node,
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0
    ) {
        DomLocalRoot(parent, stylesheet, seed).apply {
            generate(this)
        }.build()
    }
}

/** Used to locate generator classes visible in the UI. */
@Target(AnnotationTarget.CLASS)
annotation class PublicGenerator(val name: String)

/** A slot on the generator that accepts another generator. */
@Target(AnnotationTarget.PROPERTY)
annotation class GeneratorSlot(val name: String)