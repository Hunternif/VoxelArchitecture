package hunternif.voxarch.dom.builder

import hunternif.voxarch.builder.BLD_SLOPE_ROOF
import hunternif.voxarch.dom.slope
import hunternif.voxarch.dom.space
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.ClipMask
import hunternif.voxarch.plan.Slope
import hunternif.voxarch.plan.SlopedRoof
import hunternif.voxarch.plan.naturalDepth
import hunternif.voxarch.util.roundUpToEven

/**
 * Adds a gable roof with 2 slopes.
 */
class DomGableRoofDecor : DomBuilder() {
    //TODO: add roof offset, unify with DomTurretRoofDecor
    /** Y/X slope of roof slopes. */
    var roofSlopeRatio: Double = 1.0

    /** For adding stuff under the roof, clipped under its boundary. */
    val roof = DomNodeBuilder { SlopedRoof() }.apply {
        addStyle("roof_mask")
        slope("left")
        slope("right")
    }

    init {
        addChild(roof)
        addSlot("roof", roof)
        // this sloped roof is separate from "roof", because it doesn't have the clipping mask
        space("slope_container") {
            slope(BLD_SLOPE_ROOF, "left")
            slope(BLD_SLOPE_ROOF, "right")
        }
    }

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        //TODO: make sure stylesheet is modified only once

        // Round up to even size, to avoid gaps in the middle
        val slopeWidth = ctx.parentNode.naturalDepth.roundUpToEven().vx / 2
        val slopeHeight = slopeWidth * roofSlopeRatio

        // Create style rules for this instance:
        ctx.stylesheet.add {
            styleFamily(selectDescendantOf(this@DomGableRoofDecor)) {
                style(select("roof_mask"), select("slope_container")) {
                    width { 100.pct }
                    depth { 100.pct }
                    height { slopeHeight }
                    alignY { above() }
                }
                style(select(roof)) {
                    clipMask { set(ClipMask.BOUNDARY) }
                }
                styleFor<Slope> {
                    width { slopeWidth }
                    depth { 100.pct }
                    height { 100.pct }
                    alignX { center() }
                }
                style("left") {
                    rotation { set(-90.0) }
                    alignZ { northIn() }
                }
                style("right") {
                    rotation { set(90.0) }
                    alignZ { southIn() }
                }
            }
        }
        return super.getChildrenForLayout(ctx)
    }
}