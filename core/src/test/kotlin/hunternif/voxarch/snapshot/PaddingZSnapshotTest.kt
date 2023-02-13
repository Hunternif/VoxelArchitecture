package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.FillBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.storage.BlockData
import org.junit.Test

class PaddingZSnapshotTest : BaseSnapshotTest(1, 10, 10, mapOf(
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
    fun `padding mixed yz`() {
        testPadding {
            paddingY { 1.vx }
            paddingBackZ { 1.vx }
            paddingFrontZ { 60.pct }
        }
    }

    @Test
    fun `padding z rotated 90`() {
        testPadding {
            size(10.vx, 10.vx, 1.vx)
            rotation { set(-90.0) }
            paddingBackZ { 20.pct }
            paddingFrontZ { 10.pct }
        }
    }

    private fun testPadding(
        styleBlock: Rule.() -> Unit,
    ) {
        val style = defaultStyle.add {
            style("parent") {
                size(1.vx, 10.vx, 10.vx)
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
        record(out.sliceX(0))
    }
}