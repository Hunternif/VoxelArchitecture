package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder
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
    fun <V : Value<*>> add(prop: Property<V>, value: V) {
        declarations.add(Declaration(prop, value))
    }
}

/**
 * Represents a single property-value pair, e.g.: `width: 100%`.
 *
 * Note that [value] is just a "written description" in the stylesheet.
 * At runtime it will be calculated and applied to the Node/Generator.
 */
class Declaration<V : Value<*>>(
    val property: Property<V>,
    val value: V,
) {
    fun applyTo(styled: StyledElement) {
        property.applyTo(styled, value)
    }
}

/**
 * [destType] is either a [Node] or a [IGenerator].
 *
 * Accepts only a specific type of value [valType], e.g.:
 * - property 'width' accepts a [Dimension].
 * - property 'shape' accepts enum [PolygonShape].
 */
abstract class Property<V : Value<*>>(
    val name: String,
    val destType: Class<*>,
    val valType: Class<V>,
) {
    abstract fun applyTo(styled: StyledElement, value: V)
}

/**
 * At runtime this will be invoked, and the result will be applied to the
 * styled property of a Node/Generator.
 */
interface Value<T> {
    operator fun invoke(base: T, seed: Long): T
}


// ============================================================================
// Helpers to simplify implementation of properties for Nodes and Generators.
// ============================================================================

/** Helper function for creating a new [Property] for a [Node] class */
internal inline fun <reified N : Node, reified V : Value<*>> newNodeProperty(
    name: String,
    noinline block: StyledNode<N>.(V) -> Unit,
): Property<V> {
    return object : Property<V>(name, N::class.java, V::class.java) {
        override fun applyTo(styled: StyledElement, value: V) {
            if (styled is StyledNode<*> &&
                destType.isAssignableFrom(styled.domBuilder.node.javaClass)
            ) {
                @Suppress("UNCHECKED_CAST")
                (styled as StyledNode<N>).block(value)
            }
        }
    }
}

/** Helper function for creating a new [Property] for a [IGenerator] class */
internal inline fun <reified G : IGenerator, reified V : Value<*>> newGenProperty(
    name: String,
    noinline block: StyledGen<G>.(V) -> Unit,
): Property<V> {
    return object : Property<V>(name, G::class.java, V::class.java) {
        override fun applyTo(styled: StyledElement, value: V) {
            if (styled is StyledGen<*> &&
                destType.isAssignableFrom(styled.gen.javaClass)
            ) {
                @Suppress("UNCHECKED_CAST")
                (styled as StyledGen<G>).block(value)
            }
        }
    }
}

/** Helper function for creating a new property that applies to any DomBuilder */
internal inline fun <reified V : Value<*>> newDomProperty(
    name: String,
    noinline block: StyledElement.(V) -> Unit,
): Property<V> {
    return object : Property<V>(name, DomBuilder::class.java, V::class.java) {
        override fun applyTo(styled: StyledElement, value: V) {
            styled.block(value)
        }
    }
}
