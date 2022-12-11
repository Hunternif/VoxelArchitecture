package hunternif.voxarch.util

/**
 * Allows to safely iterate over a cycle in a graph up to the [limit]
 * number of times, preventing infinite loop.
 *
 * At each graph node, the code that continues execution must be wrapped
 * with [guard].
 */
class CycleCounter(
    var limit: Int = 20,
) {
    /** Maps graph node to its current recursion count. */
    @PublishedApi internal val cycles = mutableMapOf<Any, Int>()

    /**
     * Increments the counter for the given object,
     * and if its below limit, executes block.
     */
    inline fun guard(
        graphNode: Any,
        crossinline block: () -> Unit,
    ) {
        val count = cycles.getOrDefault(graphNode, 0)
        cycles[graphNode] = count + 1
        if (count + 1 <= limit) {
            block()
        }
    }

    /** Resets all counts. */
    fun clear() {
        cycles.clear()
    }
}