package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node

/**
 * [destType] is either a [Node] or a [DomBuilder].
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
    val isEnum: Boolean = isType<Enum<*>>()
    val isString: Boolean = isType<String>()
    val isNumber: Boolean = Number::class.java.isAssignableFrom(valType)

    abstract fun applyTo(styled: StyledElement<*>, value: Value<T>)
    inline fun <reified T2> isType(): Boolean =
        valType.isAssignableFrom(T2::class.java)

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T2> asType(): Property<T2>? =
        if (isType<T2>()) this as Property<T2>
        else null

    @Suppress("UNCHECKED_CAST")
    fun asEnum(): Property<Enum<*>>? =
        if (isType<Enum<*>>()) this as Property<Enum<*>>
        else null

    override fun toString(): String = name
}

// ============================================================================
// Helpers to simplify implementation of properties for Nodes and DomBuilders.
// ============================================================================

/** Helper function for creating a new [Property] for a [Node] class */
internal inline fun <reified N : Node, reified T> newNodeProperty(
    name: String,
    default: T,
    noinline block: StyledNode<N>.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, N::class.java, T::class.java, default) {
        override fun applyTo(styled: StyledElement<*>, value: Value<T>) {
            if (styled is StyledNode<*> &&
                destType.isAssignableFrom(styled.domBuilder.nodeClass)
            ) {
                @Suppress("UNCHECKED_CAST")
                (styled as StyledNode<N>).block(value)
            }
        }
    }
}

/** Helper function for creating a new [Property] for a [DomBuilder] class */
internal inline fun <reified D : DomBuilder, reified T> newDomProperty(
    name: String,
    default: T,
    noinline block: StyledElement<D>.(Value<T>) -> Unit,
): Property<T> {
    return object : Property<T>(name, D::class.java, T::class.java, default) {
        override fun applyTo(styled: StyledElement<*>, value: Value<T>) {
            if (destType.isAssignableFrom(styled.domBuilder::class.java)) {
                @Suppress("UNCHECKED_CAST")
                (styled as StyledElement<D>).block(value)
            }
        }
    }
}
