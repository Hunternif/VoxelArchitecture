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
class DomGableRoofDecor : DomBuilder(), IRoofDomBuilder {
    //TODO: add roof offset, unify with DomTurretRoofDecor
    /** Y/X slope of roof slopes. */
    var roofSlopeRatio: Double = 1.0

    override var roofOffset = 1

    /** For adding stuff under the roof, clipped under its boundary. */
    val roof = DomNodeBuilder { SlopedRoof() }.apply {
        addStyle(ROOF_MASK)
        slope(LEFT_SLOPE)
        slope(RIGHT_SLOPE)
    }

    init {
        addChild(roof)
        addSlot("roof", roof)
        // this sloped roof is separate from "roof", because it doesn't have the clipping mask
        space(ROOF_CONTAINER) {
            slope(BLD_SLOPE_ROOF, LEFT_SLOPE)
            slope(BLD_SLOPE_ROOF, RIGHT_SLOPE)
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
                style(select(ROOF_CONTAINER), select(ROOF_MASK)) {
                    width { 100.pct }
                    depth { 100.pct }
                    height { slopeHeight }
                    alignY { above() }
                }
                style(ROOF_MASK) {
                    clipMask { set(ClipMask.BOUNDARY) }
                }
                style(ROOF_CONTAINER) {
                    width { 100.pct + roofOffset.vx * 2 }
                }
                styleFor<Slope> {
                    width { slopeWidth }
                    depth { 100.pct }
                    height { 100.pct }
                    alignX { center() }
                }
                style(LEFT_SLOPE) {
                    rotation { set(-90.0) }
                    alignZ { northIn() }
                }
                style(RIGHT_SLOPE) {
                    rotation { set(90.0) }
                    alignZ { southIn() }
                }
            }
        }
        return super.getChildrenForLayout(ctx)
    }

    companion object {
        const val LEFT_SLOPE = "left"
        const val RIGHT_SLOPE = "right"
        const val ROOF_CONTAINER = "roof_container"
        const val ROOF_MASK = "roof_mask"
    }
}