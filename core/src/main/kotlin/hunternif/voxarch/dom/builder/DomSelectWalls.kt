package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.select
import hunternif.voxarch.dom.style.set
import hunternif.voxarch.plan.Wall

/**
 * Provides slots to attach new DOM elements to 4 walls of a Room.
 * Adds intermediate dummy Wall nodes to align children to the walls.
 */
class DomSelectWalls : DomBuilder() {
    /** negative Z */
    val north = DomTempNodeBuilder { Wall() }

    /** positive Z */
    val south = DomTempNodeBuilder { Wall() }

    /** positive X */
    val east = DomTempNodeBuilder { Wall() }

    /** negative X */
    val west = DomTempNodeBuilder { Wall() }

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
            style(select(north)) {
                alignX { center() }
                alignZ { northIn() }
                // default rotation
            }
            style(select(south)) {
                alignX { center() }
                alignZ { southIn() }
                rotation { set(180.0) }
            }
            style(select(east)) {
                alignX { eastIn() }
                alignZ { center() }
                rotation { set(-90.0) }
            }
            style(select(west)) {
                alignX { westIn() }
                alignZ { center() }
                rotation { set(90.0) }
            }
        }

        return super.getChildrenForLayout(ctx)
    }
}