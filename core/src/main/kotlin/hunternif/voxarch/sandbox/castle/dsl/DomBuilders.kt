package hunternif.voxarch.sandbox.castle.dsl

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure

@DslMarker
annotation class CastleDsl

/** Base class for DOM elements. Build your DOM starting from [DomRoot]! */
@CastleDsl
abstract class DomBuilder {
    internal abstract val root: DomRoot
    internal abstract val parent: DomBuilder
    internal abstract val seed: Long
    /** Can be null for logical parts of DOM that don't correspond to a Node*/
    internal abstract val node: Node?
    internal val children = mutableListOf<DomBuilder>()
    /** Recursively invokes this method on children. */
    internal abstract fun build(): Node?
}

/** Root of the DOM.
 * @param stylesheet this stylesheet will apply to all elements in this DOM.
 * @param seed each child element will receive a seed value that's derived
 *             from this root seed value by a deterministic arithmetic.
 */
class DomRoot(
    internal val stylesheet: Stylesheet = Stylesheet(),
    override val seed: Long = 0
) : DomBuilder() {
    override val parent: DomBuilder = this
    override val node: Node = Structure()
    override val root get() = this

    public override fun build(): Node {
        children.forEach { it.build() }
        return node
    }
}

/** Represents any nodes below the root. */
private class NodeDomBuilder(
    private val styleClass: Array<out String>,
    override val parent: DomBuilder,
    override val seed: Long,
    private val createNode: DomBuilder.() -> Node
) : DomBuilder() {

    override val root: DomRoot by lazy { parent.root }
    override var node: Node? = null
    override fun build(): Node {
        val node = createNode()
        this.node = node
        findParentNode().addChild(node)
        root.stylesheet.apply(this, styleClass)
        children.forEach { it.build() }
        return node
    }
}

/**
 * Finds the most immediate (lowest) non-null parent [Node].
 * Non-null because root has a non-null node.
 */
private fun DomBuilder.findParentNode(): Node {
    var domBuilder = this.parent
    while (domBuilder != root)
    {
        val node = domBuilder.node
        if (node != null) return node
        domBuilder = domBuilder.parent
    }
    return root.node
}

/** Creates a child [DomBuilder], adds it to parent and returns. */
internal fun DomBuilder.createChild(
    styleClass: Array<out String>,
    createNode: DomBuilder.() -> Node
): DomBuilder {
    val childSeed = seed + children.size + 1
    val child = NodeDomBuilder(styleClass, this, childSeed, createNode)
    children.add(child)
    return child
}