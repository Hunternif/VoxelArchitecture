package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
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
    open fun build() {
        generators.forEach { it.generate(this) }
        children.forEach { it.build() }
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
        build()
        return node
    }
}

/** Represents any nodes below the root. */
open class DomNodeBuilder<out N: Node>(
    ctx: DomContext,
    private val createNode: () -> N
) : DomBuilder(ctx) {
    private val styleClass = LinkedHashSet<String>()
    val node: N by lazy { createNode() }
    override fun build() {
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
 * If there is a cycle, returns the root node.
 * Non-null because root has a non-null node.
 */
fun DomBuilder.findParentNode(): Node {
    // detect cycles
    val visited = mutableSetOf(this)
    var dom = this.parent
    while (dom !in visited) {
        if (dom is DomNodeBuilder<*>) return dom.node
        visited.add(dom)
        dom = dom.parent
    }
    return dom.ctx.rootNode
}

/** Checks if this builder builds the right class of node and casts to it*/
@Suppress("UNCHECKED_CAST")
inline fun <reified N2 : Node> DomBuilder.asNodeBuilder(): DomNodeBuilder<N2>? =
    if (this is DomNodeBuilder<*> && node is N2) this as DomNodeBuilder<N2>
    else null

internal fun DomBuilder.nextChildSeed() = seed + children.size + 1

enum class Visibility {
    VISIBLE,
    /** The node's children will not be created. */
    GONE
}