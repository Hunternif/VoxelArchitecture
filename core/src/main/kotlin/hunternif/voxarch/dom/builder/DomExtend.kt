package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.selectChildOf
import hunternif.voxarch.dom.style.set

/**
 * Provides slots to attach new DOM elements to 4 walls of a Room.
 */
class DomExtend : DomBuilder() {
    /** negative Z */
    val north = DomBuilder()

    /** positive Z */
    val south = DomBuilder()

    /** positive X */
    val east = DomBuilder()

    /** negative X */
    val west = DomBuilder()

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
            style(selectChildOf(north).instances(north.children)) {
                alignX { center() }
                alignZ { northOut() }
                rotation { set(90.0) }
            }
            style(selectChildOf(south).instances(south.children)) {
                alignX { center() }
                alignZ { southOut() }
                rotation { set(-90.0) }
            }
            style(selectChildOf(east).instances(east.children)) {
                alignZ { center() }
                alignX { eastOut() }
                // default rotation
            }
            style(selectChildOf(west).instances(west.children)) {
                alignZ { center() }
                alignX { westOut() }
                rotation { set(180.0) }
            }
        }

        return super.getChildrenForLayout(ctx)
    }
}