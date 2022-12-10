package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node
import java.util.*

/** Base class for DOM elements. */
@CastleDsl
abstract class DomBuilder(val ctx: DomContext) {
    var stylesheet: Stylesheet = defaultStyle
    var parent: DomBuilder = ctx.rootBuilder
         protected set
    internal var seed: Long = 0
    /** Don't manually add children, use [addChild] instead.*/
    internal val children = mutableListOf<DomBuilder>()
    /** Whether the node and its children will be built or ignored. */
    internal var visibility: Visibility = Visibility.VISIBLE
    /** Generators add more child DomBuilders.
     * Must be called prior to [build], to prevent concurrent modification. */
    internal val generators = mutableListOf<IGenerator>()
    /** Recursively invokes this method on children. */
    open fun build(parentNode: Node) {
        generators.forEach { it.generate(this, parentNode) }
        children.forEach { it.build(parentNode) }
    }
    internal open fun addChild(
        child: DomBuilder,
        childSeed: Long = nextChildSeed()
    ) {
        child.parent = this
        child.seed = childSeed
        child.stylesheet = stylesheet
        children.add(child)
    }
}

/**
 * Represents context shared by all DOM builders.
 */
class DomContext(
    stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0L,
    val rootNode: Node,
) {
    constructor(root: Node) : this(defaultStyle, 0L, root)
    val rootBuilder = DomRoot(stylesheet, seed, ctx=this)
}

/** Root of the DOM.
 * @param stylesheet this stylesheet will apply to all elements in this DOM.
 * @param seed each child element will receive a seed value that's derived
 *             from this root seed value by a deterministic arithmetic.
 */
class DomRoot(
    stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0,
    ctx: DomContext,
) : DomBuilder(ctx) {
    init {
        this.seed = seed
        this.stylesheet = stylesheet
        this.parent = this
    }
    val node: Node get() = ctx.rootNode
    /** Builds the entire DOM tree. */
    fun buildDom(): Node {
        build(node)
        return node
    }
}

/** Represents any nodes below the root. */
open class DomNodeBuilder<N: Node>(
    ctx: DomContext,
    val nodeClass: Class<N>,
    private val createNode: () -> N
) : DomBuilder(ctx) {
    companion object {
        inline operator fun <reified N : Node> invoke(
            ctx: DomContext,
            noinline createNode: () -> N,
        ): DomNodeBuilder<N> =
            DomNodeBuilder(ctx, N::class.java, createNode)
    }
    private val styleClass = LinkedHashSet<String>()

    /** Last node built by this builder during execution. */
    var lastNode: N? = null

    override fun build(parentNode: Node) {
        val node = createNode()
        lastNode = node
        node.tags += styleClass
        parentNode.addChild(node)
        val styled = StyledNode(node, parentNode, this)
        stylesheet.applyStyle(styled, styleClass)
        if (visibility == Visibility.VISIBLE) {
            buildNode(node)
            generators.forEach { it.generate(this, node) }
            children.forEach { it.build(node) }
        } else {
            // add and then remove the node, because it needs a parent to
            // calculate styles including visibility.
            node.parent?.removeChild(node)
        }
    }
    /** Any custom initialization code for this node.
     * Don't use it to add child nodes, create a IGenerator for that instead. */
    open fun buildNode(node: N) {}
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

/** Checks if this builder builds the right class of node and casts to it*/
@Suppress("UNCHECKED_CAST")
inline fun <reified N2 : Node> DomBuilder.asNodeBuilder(): DomNodeBuilder<N2>? =
    if (this is DomNodeBuilder<*> &&
        N2::class.java.isAssignableFrom(nodeClass)
    ) this as DomNodeBuilder<N2>
    else null

internal fun DomBuilder.nextChildSeed() = seed + children.size + 1

enum class Visibility {
    VISIBLE,
    /** The node's children will not be created. */
    GONE
}