package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.property.*

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
    }

    override fun build(ctx: DomBuildContext) {
        val styled = StyledElement(this, ctx)
        ctx.stylesheet.applyStyle(styled)

        onlyOnce {
            ctx.stylesheet.add {
                if (north.children.any()) {
                    styleFor(*north.children.toTypedArray()) {
                        alignX { center() }
                        alignZ { northOut() }
                    }
                }
                if (south.children.any()) {
                    styleFor(*south.children.toTypedArray()) {
                        alignX { center() }
                        alignZ { southOut() }
                    }
                }
                if (east.children.any()) {
                    styleFor(*east.children.toTypedArray()) {
                        alignZ { center() }
                        alignX { eastOut() }
                    }
                }
                if (west.children.any()) {
                    styleFor(*west.children.toTypedArray()) {
                        alignZ { center() }
                        alignX { westOut() }
                    }
                }
            }
        }

        children.forEach { it.build(ctx.makeChildCtx()) }
    }
}