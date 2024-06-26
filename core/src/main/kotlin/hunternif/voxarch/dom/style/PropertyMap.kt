package hunternif.voxarch.dom.style

/**
 * Maps style properties to declarations, ensuring generic type safety.
 * Contains only 1 declaration for each property type.
 */
class PropertyMap(declarations: Collection<Declaration<*>>) {
    private val map: Map<Property<*>, Declaration<*>> =
        LinkedHashMap<Property<*>, Declaration<*>>().apply {
            declarations.forEach {
                // Later occurrences must override the position of earlier ones,
                // to maintain style order:
                if (it.property in this) remove(it.property)
                put(it.property, it)
            }
        }

    val values: Collection<Declaration<*>> get() = map.values

    operator fun <T> get(property: Property<T>): Declaration<T>? {
        @Suppress("UNCHECKED_CAST")
        return map[property] as Declaration<T>?
    }

    operator fun contains(property: Property<*>): Boolean =
        property in map.keys

    inline fun forEach(action: (Declaration<*>) -> Unit) {
        for (element in values) action(element)
    }

    companion object {
        fun Collection<Declaration<*>>.toPropertyMap() = PropertyMap(this)
    }
}