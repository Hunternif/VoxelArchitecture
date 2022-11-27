package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.Stylesheet.Companion.GLOBAL_STYLE
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

/**
 * The constructor arguments are the "CSS selector" for this rule.
 * @param styleClass is the "CSS class".
 * @param destType is the optional "CSS tag" (a node or a generator).
 */
@CastleDsl
class Rule(
    var styleClass: String = GLOBAL_STYLE,
    var destType: Class<*>? = null,
) {
    val declarations = mutableListOf<Declaration<*>>()
    fun <V> add(prop: Property<V>, value: IStyleValue<V>) {
        declarations.add(Declaration(prop, value))
    }
}

/**
 * Represents a single property-value pair, e.g.: `width: 100%`.
 *
 * Note that [value] is just a "written description" in the stylesheet,
 * not the calculated value [V] that's applied to the Node/Generator.
 */
class Declaration<V>(
    val property: Property<V>,
    val value: IStyleValue<V>,
) {
    fun applyTo(styled: StyledElement) {
        property.applyTo(styled, value.get())
    }
}

/**
 * The value as a "written description" in the stylesheet,
 * not the calculated value [V] that's applied to the Node/Generator.
 */
interface IStyleValue<V> {
    fun get(): V
}

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
    abstract fun applyTo(styled: StyledElement, value: V)
}


// ============================================================================
// Below are helper abstract base classes to simplify implementation
// of properties for Nodes and Generators.
// ============================================================================

abstract class NodeProperty<N : Node, V>(
    nodeType: Class<N>,
    valType: Class<V>,
) : Property<V>(nodeType, valType) {
    override fun applyTo(styled: StyledElement, value: V) {
        if (styled is StyledNode<*> &&
            destType.isAssignableFrom(styled.domBuilder.node.javaClass)
        ) {
            @Suppress("UNCHECKED_CAST")
            applyToNode(styled as StyledNode<N>, value)
        }
    }

    abstract fun applyToNode(styled: StyledNode<N>, value: V)
}


abstract class GenProperty<G : IGenerator, V>(
    genType: Class<G>,
    valType: Class<V>,
) : Property<V>(genType, valType) {
    override fun applyTo(styled: StyledElement, value: V) {
        if (styled is StyledGen<*> &&
            destType.isAssignableFrom(styled.gen.javaClass)
        ) {
            @Suppress("UNCHECKED_CAST")
            applyToGen(styled as StyledGen<G>, value)
        }
    }

    abstract fun applyToGen(styled: StyledGen<G>, value: V)
}


/** Helper function for creating a new [NodeProperty] */
internal inline fun <reified N : Node, reified V> newNodeProperty(
    noinline block: StyledNode<N>.(V) -> Unit,
): NodeProperty<N, V> {
    return object : NodeProperty<N, V>(N::class.java, V::class.java) {
        override fun applyToNode(styled: StyledNode<N>, value: V) {
            styled.block(value)
        }
    }
}

/** Helper function for creating a new [GenProperty] */
internal inline fun <reified G : IGenerator, reified V> newGenProperty(
    noinline block: StyledGen<G>.(V) -> Unit,
): GenProperty<G, V> {
    return object : GenProperty<G, V>(G::class.java, V::class.java) {
        override fun applyToGen(styled: StyledGen<G>, value: V) {
            styled.block(value)
        }
    }
}