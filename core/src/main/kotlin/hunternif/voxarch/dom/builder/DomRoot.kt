package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.collapse
import java.util.LinkedList

/** Root of the DOM. */
class DomRoot : DomBuilder() {

    private val listeners = mutableListOf<IDomListener>()

    fun addListener(listener: IDomListener) {
        listeners.add(listener)
    }

    /** Builds the entire DOM tree. */
    fun buildDom(
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0L,
        rootNode: Node = Structure(),
        maxRecursions: Int = 4,
        cleanDummies: Boolean = true,
        hinting: Boolean = false,
    ): Node {
        // Building happens in multiple passes:
        // 1. Measure: each of the immediate children produces its StyledElement.
        // 2. Layout: apply styles and modify properties as necessary,
        //      for layout container elements.
        //      This could create new instances.
        // 3. Recurse: continue the process for each of the children.
        //
        // General invariant: when a parent element is built, it becomes
        // immutable, i.e. its children will never modify it.

        val stats = DomBuildStats()
        val rootCtx = DomBuildContext(rootNode, stylesheet, seed, stats)
        val rootElement = prepareForLayout(rootCtx)

        // This queue contains fully completed parent elements,
        // with their children not prepared yet:
        val layoutQueue = LinkedList<StyledElement<*>>()
        layoutQueue.add(rootElement)

        while (layoutQueue.isNotEmpty()) {
            val element = layoutQueue.pop()
            listeners.forEach { it.onBeginBuild(element) }
            // 1. Measure
            val styledChildren = element.prepareChildren(maxRecursions)
            listeners.forEach { it.onPrepareChildren(element, styledChildren) }
            styledChildren.forEach { stylesheet.applyStyle(it) }
            // 1.1. Hide invisible
            val visibleChildren = styledChildren.filter {
                if (!it.isVisible) it.cleanup()
                it.isVisible
            }
            // 2. Layout
            val laidOutChildren = element.domBuilder.layout(visibleChildren)
            listeners.forEach { it.onLayoutChildren(element, laidOutChildren) }
            // 3. Recurse
            laidOutChildren.forEach {
                it.postLayout()
                layoutQueue.add(it)
            }
            listeners.forEach { it.onEndBuild(element) }
        }

        // Apply hinting
        if (hinting) {
            // Assuming parents are added before theirr children.
            //TODO: perform topological sort just in case
            stats.hints.forEach { (node, hintDir) ->
                node.origin = findOriginHint(node, hintDir)
            }
        }
        // Collapse dummy nodes:
        if (cleanDummies) {
            stats.dummyNodes.forEach { it.collapse() }
        }
        // Remove added DOM elements to keep the tree unchanged:
        stats.addedContent.forEach { (parent, child) ->
            parent.removeChild(child)
        }

        return rootNode
    }

    /** Prepares children except those that exceed recursion limit. */
    private fun StyledElement<*>.prepareChildren(
        maxRecursions: Int,
    ): List<StyledElement<*>> {
        if (children.isEmpty()) return emptyList()
        val ctx = makeChildCtx()
        val children = domBuilder.getChildrenForLayout(ctx)
        val result = ArrayList<StyledElement<*>>(children.count())
        children.forEachIndexed { i, child ->
            val recursions = ctx.lineage.count { it.domBuilder == child }
            if (recursions < maxRecursions) {
                val childCtx = ctx.copy(seed = seed + i + 1)
                result.add(child.prepareForLayout(childCtx))
            }
        }
        return result
    }
}