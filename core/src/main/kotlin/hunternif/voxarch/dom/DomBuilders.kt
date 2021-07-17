package hunternif.voxarch.dom

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure

@DslMarker
annotation class CastleDsl

/** Base class for DOM elements. Build your DOM starting from [DomRoot]! */
@CastleDsl
abstract class DomBuilder<out N: Node?> {
    internal open val stylesheet: Stylesheet by lazy { parent.stylesheet }
    internal abstract val parent: DomBuilder<Node?>
    internal abstract val seed: Long
    /** Can be null for logical parts of DOM that don't correspond to a Node*/
    internal abstract val node: N
    internal val children = mutableListOf<DomBuilder<Node?>>()
    /** Recursively invokes this method on children. */
    internal open fun build(): N {
        children.forEach { it.build() }
        return node
    }
}

/** Root of the DOM.
 * @param stylesheet this stylesheet will apply to all elements in this DOM.
 * @param seed each child element will receive a seed value that's derived
 *             from this root seed value by a deterministic arithmetic.
 */
class DomRoot(
    override val stylesheet: Stylesheet = Stylesheet(),
    override val seed: Long = 0
) : DomBuilder<Structure>() {
    override val parent: DomBuilder<Node> = this
    override val node = Structure()

    /** Builds the entire DOM tree. */
    public override fun build(): Structure = super.build()
}

/** Represents any nodes below the root. */
open class DomNodeBuilder<out N: Node>(
    internal val styleClass: Collection<String>,
    override val parent: DomBuilder<Node?>,
    override val seed: Long,
    private val createNode: DomBuilder<N>.() -> N
) : DomBuilder<N>() {
    override val node: N by lazy { createNode() }
    override fun build(): N {
        findParentNode().addChild(node)
        stylesheet.apply(this, styleClass)
        children.forEach { it.build() }
        return node
    }
}

/**
 * Finds the most immediate (lowest) non-null parent [Node].
 * Non-null because root has a non-null node.
 */
internal fun DomBuilder<Node?>.findParentNode(): Node {
    var domBuilder = this.parent
    while (domBuilder !is DomRoot)
    {
        val node = domBuilder.node
        if (node != null) return node
        domBuilder = domBuilder.parent
    }
    return domBuilder.node
}

internal fun DomBuilder<Node?>.nextChildSeed() = seed + children.size + 1

/** Creates a child [DomBuilder], adds it to parent and returns. */
internal fun <N: Node> DomBuilder<Node?>.createChild(
    styleClass: Array<out String>,
    createNode: DomBuilder<N>.() -> N
) : DomBuilder<N> {
    val child = DomNodeBuilder(
        styleClass.toList(),
        this,
        nextChildSeed(),
        createNode
    )
    children.add(child)
    return child
}