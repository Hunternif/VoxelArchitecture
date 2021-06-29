package hunternif.voxarch.sandbox.castle.dsl

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.vector.Vec3

@DslMarker
annotation class CastleDsl

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

/** Root of the DOM. */
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

/**
 * Finds the most immediate (lowest) non-null parent [Node].
 * Non-null because root has a non-null node.
 */
internal fun DomBuilder.findParentNode(): Node {
    var domBuilder = this.parent
    while (domBuilder != root)
    {
        val node = domBuilder.node
        if (node != null) return node
        domBuilder = domBuilder.parent
    }
    return root.node
}

/** Represents abstract empty [Node].*/
open class NodeDomBuilder(
    private val styleClass: Array<out String>,
    override val parent: DomBuilder,
    override val seed: Long
) : DomBuilder() {
    override val root: DomRoot by lazy { parent.root }
    override var node: Node? = null
    override fun build(): Node {
        val node = buildNode()
        this.node = node
        findParentNode().addChild(node)
        root.stylesheet.apply(this, styleClass)
        children.forEach { it.build() }
        return node
    }
    internal open fun buildNode(): Node = Node(Vec3.ZERO)
}

open class RoomDomBuilder(
    styleClass: Array<out String>,
    parent: DomBuilder,
    seed: Long
) : NodeDomBuilder(styleClass, parent, seed) {
    override fun buildNode(): Node = Room(Vec3.ZERO, Vec3.ZERO)
}

fun DomBuilder.node(
    vararg styleClass: String,
    block: DomBuilder.() -> Unit = {}
) {
    val childSeed = seed + children.size + 1
    children.add(
        NodeDomBuilder(styleClass, this, childSeed).apply(block)
    )
}

fun DomBuilder.room(
    vararg styleClass: String,
    block: DomBuilder.() -> Unit = {}
) {
    val childSeed = seed + children.size + 1
    children.add(
        RoomDomBuilder(styleClass, this, childSeed).apply(block)
    )
}
