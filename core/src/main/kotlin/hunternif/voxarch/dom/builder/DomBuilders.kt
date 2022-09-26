package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import java.util.*

/** Base class for DOM elements. Build your DOM starting from [DomRoot]! */
@CastleDsl
abstract class DomBuilder<out N: Node?> {
    var stylesheet: Stylesheet = defaultStyle
    internal var parent: DomBuilder<Node?> = DetachedRoot
        private set
    internal var seed: Long = 0
    /** Can be null for logical parts of DOM that don't correspond to a Node*/
    abstract val node: N
    /** Don't manually add children, use [addChild] instead.*/
    internal val children = mutableListOf<DomBuilder<Node?>>()
    /** Whether the node and its children will be built or ignored. */
    internal var visibility: Visibility = Visibility.VISIBLE
    /** Recursively invokes this method on children. */
    /** Generators add more child DomBuilders. */
    internal val generators = mutableListOf<IGenerator>()
    internal open fun build(): N {
        generators.forEach { it.generate(this) }
        children.forEach { it.build() }
        return node
    }
    internal open fun addChild(
        child: DomBuilder<Node?>,
        childSeed: Long = nextChildSeed()
    ) {
        child.parent = this
        child.seed = childSeed
        child.stylesheet = stylesheet
        children.add(child)
    }
    /** Checks if this builder builds the right class of node and casts to it*/
    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified N2 : Node?> asBuilder(): DomBuilder<N2>? {
        return if (node is N2) this as DomBuilder<N2>
        else null
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
    stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0,
) : DomBuilder<Structure>() {
    override val node = Structure()
    init {
        this.seed = seed
        this.stylesheet = stylesheet
    }
    /** Builds the entire DOM tree. */
    public override fun build(): Structure = super.build()
}

/** Used for creating a local DOM within a Node tree,
 * e.g. to change the stylesheet locally. */
class DomLocalRoot<out N : Node?>(
    override val node: N,
    private val localStylesheet: Stylesheet = defaultStyle,
    seed: Long = 0,
) : DomBuilder<N>() {
    init {
        this.seed = seed
        this.stylesheet = localStylesheet
    }
    public override fun build(): N = super.build()
    override fun addChild(child: DomBuilder<Node?>, childSeed: Long) {
        // pass children to the upper level
        parent.addChild(child, childSeed)
        child.stylesheet = localStylesheet
    }
}

/** Represents any nodes below the root. */
open class DomNodeBuilder<out N: Node>(
    private val createNode: () -> N
) : DomBuilder<N>() {
    private val styleClass = LinkedHashSet<String>()
    override val node: N by lazy { createNode() }
    override fun build(): N {
        node.tags += styleClass
        findParentNode().addChild(node)
        stylesheet.apply(this, styleClass)
        if (visibility == Visibility.VISIBLE) {
            buildNode()
            generators.forEach { it.generate(this) }
            children.forEach { it.build() }
        } else {
            // add and then remove the node, because it needs a parent to
            // calculate styles including visibility.
            node.parent?.removeChild(node)
        }
        return node
    }
    /** Any custom initialization code for this node.
     * Don't use it to add child nodes, create a IGenerator for that instead. */
    open fun buildNode() {}
    /** Add given style class name to this builder. */
    fun addStyle(style: String) {
        styleClass.add(style)
    }
    fun addStyles(vararg styles: String) {
        styleClass.addAll(styles)
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