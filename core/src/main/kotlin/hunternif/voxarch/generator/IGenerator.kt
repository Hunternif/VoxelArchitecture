package hunternif.voxarch.generator

import hunternif.voxarch.plan.Node

/**
 * A program that adds more child nodes to the given node.
 */
interface IGenerator {
    fun generate(parent: Node)
}

/** Used to locate generator classes visible in the UI. */
annotation class PublicGenerator