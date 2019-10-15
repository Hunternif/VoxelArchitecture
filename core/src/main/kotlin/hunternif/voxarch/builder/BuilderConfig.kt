package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node

/**
 * Register here your builders for classes and types of [Node]s.
 */
class BuilderConfig {
    private val buildersMap = mutableMapOf<Class<out Node>, NodeClassBuilders<out Node>>()

    fun <T: Node> buildersForClass(nodeClass: Class<T>): NodeClassBuilders<T> {
        @Suppress("UNCHECKED_CAST")
        return buildersMap.getOrPut(nodeClass) { NodeClassBuilders<T>() } as NodeClassBuilders<T>
    }

    /**
     * Register builders per type, for a single class of [Node].
     *
     * If you set a builder for `null` type, it will be used by default,
     * when the Node has no type, or when the builder for this type is not set.
     */
    inline fun <reified T: Node> set(vararg typeBuilder: Pair<String?, Builder<T>>) {
        buildersForClass(T::class.java).apply {
            typeBuilder.forEach { pair ->
                set(pair.first, pair.second)
            }
        }
    }

    inline fun <reified T: Node> setDefault(builder: Builder<T>) {
        buildersForClass(T::class.java).setDefault(builder)
    }

    /**
     * Get Builder for this Node given its class and type.
     * If builder is not found for type, will try default type.
     * If builder is not found for class, will try superclasses.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: Node> get(node: T): Builder<T>? {
        var nodeClass: Class<*> = node.javaClass
        var builder: Builder<*>? = null
        while (Node::class.java.isAssignableFrom(nodeClass)) {
            builder = buildersForClass(nodeClass as Class<out Node>).get(node.type)
            if (builder != null) break
            nodeClass = nodeClass.superclass
        }
        return builder as Builder<T>?
    }


    class NodeClassBuilders<T: Node> {
        private val map: MutableMap<String?, Builder<T>> = mutableMapOf()
        fun setDefault(builder: Builder<T>) { map[null] = builder }
        fun set(type: String?, builder: Builder<T>) { map[type] = builder }
        /** If builder is not found for type, will try default type. */
        fun get(type: String?): Builder<T>? = map[type] ?: map[null]
    }
}