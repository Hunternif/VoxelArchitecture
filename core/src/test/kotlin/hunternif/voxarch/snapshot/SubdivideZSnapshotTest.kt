package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.SimpleWallBuilder
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.depth
import hunternif.voxarch.dom.style.property.rotation
import hunternif.voxarch.dom.style.property.width
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.subdivide
import hunternif.voxarch.dom.wall
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import org.junit.Test

class SubdivideZSnapshotTest : BaseSnapshotTest(1, 5, 10, mapOf(
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
    fun `subdivide z mixed south`() {
        val style = makeStyle {
            style("a") { depth { 0.vx } }
            style("b") { depth { 100.pct } }
            style("c") { depth { 1.vx } }
        }
        doTest(style, SOUTH) {
            wall("b")
            wall("c")
            wall("a")
            wall("b")
            wall("b")
            wall("a")
        }
    }

    @Test
    fun `subdivide z mixed north`() {
        val style = makeStyle {
            style("a") { depth { 0.vx } }
            style("b") { depth { 100.pct } }
            style("c") { depth { 1.vx } }
        }
        doTest(style, NORTH) {
            wall("b")
            wall("c")
            wall("a")
            wall("b")
            wall("b")
            wall("a")
        }
    }

    @Test
    fun `subdivide z mixed south rotated`() {
        val style = makeStyle {
            style("a") { width { 0.vx } }
            style("b") { width { 100.pct } }
            style("c") { width { 1.vx } }
            styleFor<Wall> { rotation { set(90.0) } }
        }
        doTest(style, SOUTH) {
            wall("b")
            wall("c")
            wall("a")
            wall("b")
            wall("b")
            wall("a")
        }
    }

    @Test
    fun `subdivide z mixed north rotated`() {
        val style = makeStyle {
            style("a") { width { 0.vx } }
            style("b") { width { 100.pct } }
            style("c") { width { 1.vx } }
            styleFor<Wall> { rotation { set(90.0) } }
        }
        doTest(style, NORTH) {
            wall("b")
            wall("c")
            wall("a")
            wall("b")
            wall("b")
            wall("a")
        }
    }

    private fun makeStyle(
        depth: Int = 9,
        block: RuleBuilder.() -> Unit = {},
    ) = defaultStyle.add {
        style("container") {
            size(0.vx, 4.vx, depth.vx)
        }
        block()
    }

    private fun doTest(
        style: Stylesheet = makeStyle(),
        dir: Direction3D = UP,
        block: DomBuilder.() -> Unit = {},
    ) {
        val dom = domRoot {
            node("container") {
                subdivide(dir) {
                    block()
                }
            }
        }.buildDom(style)
        build(dom)
        record(out.sliceX(0))
    }
}