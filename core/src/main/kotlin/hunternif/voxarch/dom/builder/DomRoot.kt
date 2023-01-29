package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import java.util.LinkedList

/** Root of the DOM. */
class DomRoot(
    val node: Node = Structure(),
) : DomBuilder() {

    /** Builds the entire DOM tree. */
    fun buildDom(
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0L,
        maxRecursions: Int = 20,
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

        val rootCtx = DomBuildContext(this, node, stylesheet, seed)
            .inherit(styleClass)
        val rootElement = prepareForLayout(rootCtx)

        // This queue contains fully completed parent elements,
        // with their children not prepared yet:
        val layoutQueue = LinkedList<StyledElement<*>>()
        layoutQueue.add(rootElement)

        while (layoutQueue.isNotEmpty()) {
            val element = layoutQueue.pop()
            // 1. Measure
            val styledChildren = element.prepareChildren(maxRecursions)
            styledChildren.forEach { stylesheet.applyStyle(it) }
            // 1.1. Hide invisible
            val visibleChildren = styledChildren.filter {
                if (!it.isVisible) it.cleanup()
                it.isVisible
            }
            // 2. Layout
            val laidOutChildren = element.domBuilder.layout(visibleChildren)
            // 3. Recurse
            laidOutChildren.forEach {
                it.postLayout()
                layoutQueue.add(it)
            }
        }

        return node
    }

    /** Prepares children except those that exceed recursion limit. */
    private fun StyledElement<*>.prepareChildren(
        maxRecursions: Int,
    ): List<StyledElement<*>> {
        if (children.isEmpty()) return emptyList()
        val childCtx = makeChildCtx()
        val children = domBuilder.getChildrenForLayout(childCtx)
        val childrenBelowLimit = children.filter { child ->
            val recursions = childCtx.lineage.count { it == child }
            recursions < maxRecursions
        }
        return childrenBelowLimit.map { it.prepareForLayout(childCtx) }
    }
}