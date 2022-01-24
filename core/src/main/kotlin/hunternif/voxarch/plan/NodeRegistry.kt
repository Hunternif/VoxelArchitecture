package hunternif.voxarch.plan

import kotlin.math.max

/** Assigns a unique id to every node. */
class NodeRegistry {
    private var lastNodeIndex = -1

    private val map = mutableMapOf<Int, Node>()

    /** Creates a new id and sets it on this node, returns new id. */
    fun register(node: Node): Int {
        lastNodeIndex++
        map[lastNodeIndex] = node
        node.id = lastNodeIndex
        return lastNodeIndex
    }

    /**
     * Register the given [node] with an existing [id], e.g. when loading from
     * a file.
     * If another node already exists with the given [id], then the given [node]
     * will have its id generated.
     * Returns new id.
     */
    fun load(id: Int, node: Node): Int {
        return if (map.containsKey(id)) {
            register(node)
        } else {
            map[id] = node
            node.id = id
            lastNodeIndex = max(id, lastNodeIndex)
            id
        }
    }

    fun clear() {
        lastNodeIndex = -1
        map.clear()
    }
}