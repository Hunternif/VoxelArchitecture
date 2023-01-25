package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.property.alignX
import hunternif.voxarch.dom.style.property.center
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.dom.wall
import hunternif.voxarch.sandbox.castle.builder.ArchedWindowBuilder
import org.junit.Test

class ArchedWindowBuilderTest : BaseSnapshotTest(19, 19, 1) {
    override fun setup() {
        super.setup()
        context.builders.set("window" to ArchedWindowBuilder())
    }

    @Test
    fun `window 0x3`() {
        testWindow(0, 3)
    }

    @Test
    fun `window 1x3`() {
        testWindow(1, 3)
    }

    @Test
    fun `window 2x3`() {
        testWindow(2, 3)
    }

    @Test
    fun `window 3x3`() {
        testWindow(3, 3)
    }

    @Test
    fun `window 2x4`() {
        testWindow(2, 4)
    }

    @Test
    fun `window 3x4`() {
        testWindow(3, 4)
    }

    @Test
    fun `window 4x2`() {
        testWindow(4, 2)
    }

    @Test
    fun `window 4x6`() {
        testWindow(4, 6)
    }

    @Test
    fun `window 5x6`() {
        testWindow(5, 6)
    }

    @Test
    fun `window 6x6`() {
        testWindow(6, 6)
    }

    @Test
    fun `window 8x6`() {
        testWindow(8, 6)
    }

    @Test
    fun `window 12x16`() {
        testWindow(12, 16)
    }

    @Test
    fun `window 16x16`() {
        testWindow(16, 16)
    }

    fun testWindow(width: Int, height: Int) {
        val style = Stylesheet().add {
            style("wall") {
                size((outWidth - 1).vx, (outHeight - 1).vx, 0.vx)
            }
            style("window") {
                size(width.vx, height.vx, 0.vx)
                alignX { center() }
            }
        }
        val dom = domRoot {
            wall("wall") {
                wall("window")
            }
        }.buildDom(style)
        build(dom)
        record(out.sliceZ(0))
    }
}