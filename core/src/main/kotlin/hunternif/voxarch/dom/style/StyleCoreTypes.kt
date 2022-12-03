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
    fun <T> add(prop: Property<T>, value: Value<T>) {
        declarations.add(Declaration(prop, value))
    }
}

/**
 * Represents a single property-value pair, e.g.: `width: 100%`.
 *
 * Note that [value] is just a "written description" in the stylesheet.
 * At runtime it will be calculated and applied to the Node/Generator.
 */
class Declaration<T>(
    val property: Property<T>,
    var value: Value<T>,
) {
    fun applyTo(styled: StyledElement) {
        property.applyTo(styled, value)
    }
}

/**
 * [destType] is either a [Node] or a [IGenerator].
 *
 * Accepts only a specific type of value [valType], e.g.:
 * - property 'width' accepts a [Double].
 * - property 'shape' accepts enum [PolygonShape].
 */
abstract class Property<T>(
    val name: String,
    val destType: Class<*>,
    val valType: Class<T>,
) {
    abstract fun applyTo(styled: StyledElement, value: Value<T>)
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
internal inline fun <reified N : Node, reified T> newNodeProperty(
    name: String,
    noinline block: StyledNode<N>.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, N::class.java, T::class.java) {
        override fun applyTo(styled: StyledElement, value: Value<T>) {
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
internal inline fun <reified G : IGenerator, reified T> newGenProperty(
    name: String,
    noinline block: StyledGen<G>.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, G::class.java, T::class.java) {
        override fun applyTo(styled: StyledElement, value: Value<T>) {
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
internal inline fun <reified T> newDomProperty(
    name: String,
    noinline block: StyledElement.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, DomBuilder::class.java, T::class.java) {
        override fun applyTo(styled: StyledElement, value: Value<T>) {
            styled.block(value)
        }
    }
}
