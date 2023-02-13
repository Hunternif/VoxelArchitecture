package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.FillBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.storage.BlockData
import org.junit.Test

class PaddingXYSnapshotTest : BaseSnapshotTest(10, 10, 1, mapOf(
    "a" to 0xD7CAB5,
    "r" to 0xB13B42,
    "g" to 0x77B249,
    "b" to 0x4F6FD7,
)) {
    override fun setup() {
        super.setup()
        arrayOf("a", "r", "g", "b").forEach {
            context.builders.set(it to FillBuilder(it))
            context.materials.set(it) { BlockData(it) }
        }
    }

    @Test
    fun `padding top vx`() {
        testPadding { paddingTop { 2.vx } }
    }

    @Test
    fun `padding top pct`() {
        testPadding { paddingTop { 40.pct } }
    }

    @Test
    fun `padding bottom`() {
        testPadding { paddingBottom { 9.vx } }
    }

    @Test
    fun `padding mixed xy`() {
        testPadding {
            paddingY { 1.vx }
            paddingLeftX { 0.vx }
            paddingRightX { 50.pct }
        }
    }

    @Test
    fun `padding y squashed`() {
        testPadding {
            paddingTop { 60.pct }
            paddingBottom { 60.pct }
        }
    }

    @Test
    fun `padding clamped to parent`() {
        testPadding {
            paddingY { (-1).vx }
            paddingX { (-1).vx }
        }
    }

    @Test
    fun `padding less than available space`() {
        testPadding {
            alignY { middle() }
            alignX { center() }
            size(6.vx, 6.vx, 1.vx)
            paddingY { 1.vx }
            paddingX { 1.vx }
        }
    }

    @Test
    fun `padding more than available space`() {
        testPadding {
            alignY { middle() }
            alignX { center() }
            size(6.vx, 6.vx, 1.vx)
            paddingRightX { 50.pct }
        }
    }

    @Test
    fun `padding x rotated 90`() {
        testPadding {
            size(1.vx, 10.vx, 10.vx)
            rotation { set(90.0) }
            paddingLeftX { 20.pct }
            paddingRightX { 10.pct }
        }
    }

    private fun testPadding(
        styleBlock: Rule.() -> Unit,
    ) {
        val style = defaultStyle.add {
            style("parent") {
                size(10.vx, 10.vx, 1.vx)
            }
            style("child") {
                size(100.pct, 100.pct, 100.pct)
                styleBlock()
            }
        }
        val dom = domRoot {
            node("parent", "a") {
                node("child", "r")
            }
        }.buildDom(style)
        build(dom)
        record(out.sliceZ(0))
    }
}