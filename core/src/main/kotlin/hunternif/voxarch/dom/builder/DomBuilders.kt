package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure

/** Base class for DOM elements. Build your DOM starting from [DomRoot]! */
@CastleDsl
abstract class DomBuilder<out N: Node?> {
    internal open val stylesheet: Stylesheet by lazy { parent.stylesheet }
    internal var parent: DomBuilder<Node?> = DetachedRoot
        private set
    internal var seed: Long = 0
    /** Can be null for logical parts of DOM that don't correspond to a Node*/
    internal abstract val node: N
    /** Don't manually add children, use [addChild] instead.*/
    internal val children = mutableListOf<DomBuilder<Node?>>()
    /** Whether the node and its children will be built or ignored. */
    internal var visibility: Visibility = Visibility.VISIBLE
    /** Recursively invokes this method on children. */
    internal open fun build(): N {
        children.forEach { it.build() }
        return node
    }
    internal fun addChild(
        child: DomBuilder<Node?>,
        childSeed: Long = nextChildSeed()
    ) {
        child.parent = this
        child.seed = childSeed
        children.add(child)
    }
}

/** Placeholder for parent when a builder is not yet added as a child. */
internal object DetachedRoot : DomBuilder<Node?>() {
    override val node: Node? = null
}

/** Root of the DOM.
 * @param stylesheet this stylesheet will apply to all elements in this DOM.
 * @param seed each child element will receive a seed value that's derived
 *             from this root seed value by a deterministic arithmetic.
 */
class DomRoot(
    override val stylesheet: Stylesheet = Stylesheet(),
    seed: Long = 0
) : DomBuilder<Structure>() {
    override val node = Structure()
    init {
        this.seed = seed
    }
    /** Builds the entire DOM tree. */
    public override fun build(): Structure = super.build()
}

/** Represents any nodes below the root. */
open class DomNodeBuilder<out N: Node>(
    private val createNode: () -> N
) : DomBuilder<N>() {
    private val styleClass = mutableListOf<String>()
    override val node: N by lazy { createNode() }
    override fun build(): N {
        node.type = styleClass.firstOrNull()
        findParentNode().addChild(node)
        stylesheet.apply(this, styleClass)
        if (visibility == Visibility.VISIBLE) {
            buildNode()
            children.forEach { it.build() }
        } else {
            // add and then remove the node, because it needs a parent to
            // calculate styles including visibility.
            node.parent?.removeChild(node)
        }
        return node
    }
    /** Any custom code for adding more nodes inside this node. */
    open fun buildNode() {}
    /** Add given style class name to this builder. */
    operator fun String.unaryPlus() {
        styleClass.add(this)
    }
    /** Add given style class names to this builder. */
    operator fun Array<out String>.unaryPlus() {
        styleClass.addAll(this)
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

enum class Visibility {
    VISIBLE,
    /** The node's children will not be created. */
    GONE
}