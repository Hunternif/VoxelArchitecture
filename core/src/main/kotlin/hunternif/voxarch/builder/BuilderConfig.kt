package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node

/**
 * Register here your builders for classes and tags of [Node]s.
 */
class BuilderConfig {
    private val buildersMap = mutableMapOf<Class<out Node>, NodeClassBuilders<out Node>>()

    fun <T: Node> buildersForClass(nodeClass: Class<T>): NodeClassBuilders<T> {
        @Suppress("UNCHECKED_CAST")
        return buildersMap.getOrPut(nodeClass) { NodeClassBuilders<T>() } as NodeClassBuilders<T>
    }

    /**
     * Register builders per tag, for a single class of [Node].
     *
     * If you set a builder for `null` tag, it will be used by default,
     * when the Node has no tags, or when there are no builders for its tags.
     */
    inline fun <reified T: Node> set(vararg tagBuilder: Pair<String?, Builder<T>>) {
        buildersForClass(T::class.java).apply {
            tagBuilder.forEach { pair ->
                set(pair.first, pair.second)
            }
        }
    }

    inline fun <reified T: Node> setDefault(builder: Builder<T>) {
        buildersForClass(T::class.java).setDefault(builder)
    }

    /**
     * Get Builder for this Node given its class and tags.
     * Will try to find a builder for all tags in order.
     * If builder is not found for class, will try superclasses.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: Node> getFromConfig(node: T): Builder<T>? {
        var nodeClass: Class<*> = node::class.java
        var builder: Builder<*>? = null
        classLoop@ while (Node::class.java.isAssignableFrom(nodeClass)) {
            val buildersForClass = buildersForClass(nodeClass as Class<out Node>)
            tagLoop@ for (tag in node.tags) {
                builder = buildersForClass.get(tag)
                if (builder != null) break@classLoop
            }
            builder = buildersForClass.getDefault()
            if (builder != null) break@classLoop
            nodeClass = nodeClass.superclass
        }
        return builder as Builder<T>?
    }

    /**
     * Get Builder for this Node given its class and tags.
     * Will first check the node's inner builder instance,
     * then proceed to [get].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: Node> get(node: T): Builder<T>? {
        // 1. Try the local builder first
        // TODO: log error if type is mismatched
        node.builder?.let {
            if (it.nodeClass.isAssignableFrom(node::class.java)) {
                return it as Builder<T>
            }
        }
        // 2. Fetch appropriate builder from the config
        return getFromConfig(node)
    }


    class NodeClassBuilders<T: Node> {
        private val map: MutableMap<String?, Builder<T>> = mutableMapOf()
        fun setDefault(builder: Builder<T>) { map[null] = builder }
        fun set(tag: String?, builder: Builder<T>) { map[tag] = builder }
        fun get(tag: String?): Builder<T>? = map[tag]
        fun getDefault(): Builder<T>? = map[null]
    }
}