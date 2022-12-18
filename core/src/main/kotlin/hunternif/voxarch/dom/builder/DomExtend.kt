package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.selectInherit

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

    override fun build(ctx: DomBuildContext) {
        val styled = StyledElement(this, ctx)
        ctx.stylesheet.applyStyle(styled)

        onlyOnce {
            ctx.stylesheet.add {
                style(selectInherit(north).instances(north.children)) {
                    alignX { center() }
                    alignZ { northOut() }
                }
                style(selectInherit(south).instances(south.children)) {
                    alignX { center() }
                    alignZ { southOut() }
                }
                style(selectInherit(east).instances(east.children)) {
                    alignZ { center() }
                    alignX { eastOut() }
                }
                style(selectInherit(west).instances(west.children)) {
                    alignZ { center() }
                    alignX { westOut() }
                }
            }
        }

        children.forEach { it.build(ctx.makeChildCtx()) }
    }
}