package hunternif.voxarch.dom.style

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

    companion object {
        fun <T> defaultForProperty(property: Property<T>) =
            Declaration(property, set(property.default))
    }

    override fun toString(): String = "$property: $value"
}