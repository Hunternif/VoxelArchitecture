package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.SimpleWallBuilder
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.height
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.subdivideY
import hunternif.voxarch.dom.wall
import hunternif.voxarch.storage.BlockData
import org.junit.Test

class SubdivideSnapshotTest : BaseSnapshotTest(5, 10, 1, mapOf(
    "a" to 0x77B249,
    "b" to 0xD7CAB5,
    "c" to 0x4F6FD7,
)) {
    override fun setup() {
        super.setup()
        arrayOf("a", "b", "c").forEach {
            context.builders.set(it to SimpleWallBuilder(it))
            context.materials.set(it) { BlockData(it) }
        }
    }

    @Test
    fun `subdivide y 4 by equal 1`() {
        doTest(makeStyle(4)) {
            wall("a")
        }
    }

    @Test
    fun `subdivide y 4 by equal 2`() {
        doTest(makeStyle(4)) {
            wall("a")
            wall("b")
        }
    }

    @Test
    fun `subdivide y 4 by equal 3`() {
        doTest(makeStyle(4)) {
            wall("a")
            wall("b")
            wall("c")
        }
    }

    @Test
    fun `subdivide y 9 by equal 1`() {
        doTest(makeStyle(9)) {
            wall("a")
        }
    }

    @Test
    fun `subdivide y 9 by equal 2`() {
        doTest(makeStyle(9)) {
            wall("a")
            wall("b")
        }
    }

    @Test
    fun `subdivide y 9 by equal 3`() {
        doTest(makeStyle(9)) {
            wall("a")
            wall("b")
            wall("c")
        }
    }

    @Test
    fun `subdivide y with hole`() {
        val style = makeStyle {
            style("empty") { height { 1.vx } }
        }
        doTest(style) {
            wall("a")
            node("empty")
            wall("c")
        }
    }

    @Test
    fun `subdivide y unequal`() {
        val style = makeStyle {
            style("a") { height { 10.pct } }
            style("c") { height { 30.pct } }
        }
        doTest(style) {
            wall("a")
            wall("c")
        }
    }

    @Test
    fun `subdivide y mixed`() {
        val style = makeStyle {
            style("a") { height { 0.vx } }
            style("b") { height { 100.pct } }
            style("c") { height { 1.vx } }
        }
        doTest(style) {
            wall("a")
            wall("b")
            wall("c")
            wall("b")
        }
    }

    @Test
    fun `subdivide y mixed 2`() {
        val style = makeStyle {
            style("a") { height { 0.vx } }
            style("b") { height { 100.pct } }
            style("c") { height { 1.vx } }
        }
        doTest(style) {
            wall("b")
            wall("c")
            wall("a")
            wall("b")
            wall("b")
            wall("a")
        }
    }

    private fun makeStyle(
        height: Int = 9,
        block: RuleBuilder.() -> Unit = {},
    ) = defaultStyle.add {
        style("container") {
            size(4.vx, height.vx, 0.vx)
        }
        block()
    }

    private fun doTest(
        style: Stylesheet = makeStyle(),
        block: DomBuilder.() -> Unit = {},
    ) {
        val dom = domRoot {
            node("container") {
                subdivideY {
                    block()
                }
            }
        }.buildDom(style)
        build(dom)
        record(out.sliceZ(0))
    }
}