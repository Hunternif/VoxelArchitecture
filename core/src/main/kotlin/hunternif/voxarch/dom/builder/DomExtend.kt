package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node

/**
 * Provides slots to attach new DOM elements to 4 walls of a node,
 * and orient them so that the X axis points outward.
 */
class DomExtend : DomBuilder() {
    /** negative Z */
    val north = DomTempNodeBuilder { Node() }.apply { addStyle(TEMP_NODE_CLASS) }

    /** positive Z */
    val south = DomTempNodeBuilder { Node() }.apply { addStyle(TEMP_NODE_CLASS) }

    /** positive X */
    val east = DomTempNodeBuilder { Node() }.apply { addStyle(TEMP_NODE_CLASS) }

    /** negative X */
    val west = DomTempNodeBuilder { Node() }.apply { addStyle(TEMP_NODE_CLASS) }

    init {
        addChild(north)
        addChild(south)
        addChild(east)
        addChild(west)
        addSlot("north", north)
        addSlot("south", south)
        addSlot("east", east)
        addSlot("west", west)
    }

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        //TODO: make sure stylesheet is modified only once
        ctx.stylesheet.add {
            style(selectChildOf(TEMP_NODE_CLASS)) {
                alignX { westIn() } // snap to the outside of the parent node
                alignZ { center() }
            }
        }

        return super.getChildrenForLayout(ctx)
    }

    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        // Position the dummy nodes:
        val style = Stylesheet().add {
            style(select(north, south, east, west)) {
                size(100.pct, 100.pct, 100.pct)
            }
            style(select(north)) {
                alignX { center() }
                alignZ { northOut() }
                rotation { set(90.0) }
            }
            style(select(south)) {
                alignX { center() }
                alignZ { southOut() }
                rotation { set(-90.0) }
            }
            style(select(east)) {
                alignZ { center() }
                alignX { eastOut() }
                // default rotation
            }
            style(select(west)) {
                alignZ { center() }
                alignX { westOut() }
                rotation { set(180.0) }
            }
        }
        children.forEach { style.applyStyle(it) }
        return super.layout(children)
    }

    companion object {
        private const val TEMP_NODE_CLASS = "_extend_temp_node_"
    }
}