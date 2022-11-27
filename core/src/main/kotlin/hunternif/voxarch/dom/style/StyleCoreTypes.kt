package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.Stylesheet.Companion.GLOBAL_STYLE
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

/**
 * The constructor arguments are the "CSS selector" for this rule.
 * @param styleClass is the "CSS class".
 * @param destType is the "CSS tag" (a node or a generator).
 */
class Rule(
    var styleClass: String = GLOBAL_STYLE,
    var destType: Class<*>,
) {
    val declarations = mutableListOf<Declaration<*>>()
}

/** Represents a single property-value pair, e.g.: `width: 100%` */
class Declaration<V>(
    val property: Property<V>,
    val value: V,
)

/**
 * [destType] is either a [Node] or a [IGenerator].
 *
 * Accepts only a specific type of value [valType], e.g.:
 * - property 'width' accepts a [Dimension].
 * - property 'shape' accepts enum [PolygonShape].
 */
abstract class Property<V>(
    val destType: Class<*>,
    val valType: Class<V>,
) {
    abstract fun apply(domBuilder: DomBuilder, value: V)
    fun appliesTo(obj: Any): Boolean {
        return destType.isAssignableFrom(obj.javaClass)
    }
}


// ============================================================================
// Below are helper abstract base classes to simplify implementation
// of properties for Nodes and Generators.
// (They're actually concrete classes, in order to have a reified constructor.)
// ============================================================================

open class NodeProperty<N : Node, V>(
    nodeType: Class<N>,
    valType: Class<V>,
) : Property<V>(nodeType, valType) {
    companion object {
        inline operator fun <reified N : Node, reified V> invoke():
            NodeProperty<N, V> = NodeProperty(N::class.java, V::class.java)
    }
    override fun apply(domBuilder: DomBuilder, value: V) {
        if (domBuilder is DomNodeBuilder<*> && appliesTo(domBuilder.node)) {
            @Suppress("UNCHECKED_CAST")
            applyToNode(domBuilder as DomNodeBuilder<N>, value)
        }
    }
    /** To be overridden */
    open fun applyToNode(domBuilder: DomNodeBuilder<N>, value: V) {}
}


open class GenProperty<G : IGenerator, V>(
    genType: Class<G>,
    valType: Class<V>,
) : Property<V>(genType, valType) {
    companion object {
        inline operator fun <reified G : IGenerator, reified V> invoke():
            GenProperty<G, V> = GenProperty(G::class.java, V::class.java)
    }
    override fun apply(domBuilder: DomBuilder, value: V) {
        domBuilder.generators.forEach { gen ->
            if (appliesTo(gen)) {
                @Suppress("UNCHECKED_CAST")
                applyToGen(gen as G, domBuilder, value)
            }
        }
    }
    /** To be overridden */
    open fun applyToGen(gen: G, domBuilder: DomBuilder, value: V) {}
}