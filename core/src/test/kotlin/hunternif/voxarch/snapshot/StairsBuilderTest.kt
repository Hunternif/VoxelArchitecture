package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.DefaultBuilders
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.stairs
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.pct
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.plan.Staircase
import org.junit.Test

class StairsBuilderTest : BaseSnapshotTest(10, 10, 3) {
    override fun setup() {
        super.setup()
        context.builders.set<Staircase>("thin_stairs" to DefaultBuilders.ThinStairs)
    }

    @Test
    fun `diagonal 1x1`() {
        testStairs(10, 10)
        recordVox()
    }

    @Test
    fun `diagonal 2x1`() {
        testStairs(10, 5)
    }

    @Test
    fun `thin diagonal 1x1`() {
        testStairs(10, 10, "thin_stairs")
    }

    @Test
    fun `thin diagonal 2x1`() {
        testStairs(10, 5, "thin_stairs")
    }

    private fun testStairs(width: Int, height: Int, vararg styleClass: String) {
        val style = Stylesheet().add {
            style("container") {
                size(outWidth.vx, outHeight.vx, outDepth.vx)
            }
            styleFor<Staircase> {
                size(width.vx, height.vx, 100.pct)
            }
        }
        val dom = domRoot {
            node("container") {
                stairs(*styleClass)
            }
        }.buildDom(style, hinting = true)
        build(dom)
        record(out.sliceZ(0))
    }
}