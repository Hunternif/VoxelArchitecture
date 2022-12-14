package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

/**
 * [destType] is either a [Node] or a [IGenerator].
 *
 * Accepts only a specific type of value [valType], e.g.:
 * - property 'width' accepts a [Double].
 * - property 'shape' accepts enum [PolyShape].
 */
abstract class Property<T>(
    val name: String,
    val destType: Class<*>,
    val valType: Class<T>,
    val default: T,
) {
    abstract fun applyTo(styled: StyledElement, value: Value<T>)
    inline fun <reified T2> isType(): Boolean =
        valType.isAssignableFrom(T2::class.java)

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T2> asType(): Property<T2>? =
        if (isType<T2>()) this as Property<T2>
        else null

    override fun toString(): String = name
}

// ============================================================================
// Helpers to simplify implementation of properties for Nodes and Generators.
// ============================================================================

/** Helper function for creating a new [Property] for a [Node] class */
internal inline fun <reified N : Node, reified T> newNodeProperty(
    name: String,
    default: T,
    noinline block: StyledNode<N>.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, N::class.java, T::class.java, default) {
        override fun applyTo(styled: StyledElement, value: Value<T>) {
            if (styled is StyledNode<*> &&
                destType.isAssignableFrom(styled.domBuilder.nodeClass)
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
    default: T,
    noinline block: StyledGen<G>.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, G::class.java, T::class.java, default) {
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
    default: T,
    noinline block: StyledElement.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, DomBuilder::class.java, T::class.java, default) {
        override fun applyTo(styled: StyledElement, value: Value<T>) {
            styled.block(value)
        }
    }
}
