package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.MAT_WALL
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.property.alignX
import hunternif.voxarch.dom.style.property.center
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.dom.wall
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.builder.ArchedBridgeBuilder
import org.junit.Test

class ArchedBridgeBuilderTest : BaseSnapshotTest(19, 19, 1) {
    override fun setup() {
        super.setup()
        context.builders.set<Wall>("bridge" to ArchedBridgeBuilder(MAT_WALL))
    }

    @Test
    fun `arched bridge 1x3`() {
        testArchedBridge(1, 3)
    }

    @Test
    fun `arched bridge 2x3`() {
        testArchedBridge(2, 3)
    }

    @Test
    fun `arched bridge 3x3`() {
        testArchedBridge(3, 3)
    }


    // height = 2

    @Test
    fun `arched bridge 4x2`() {
        testArchedBridge(4, 2)
    }

    @Test
    fun `arched bridge 5x2`() {
        testArchedBridge(5, 2)
    }

    @Test
    fun `arched bridge 6x2`() {
        testArchedBridge(6, 2)
    }

    @Test
    fun `arched bridge 7x2`() {
        testArchedBridge(7, 2)
    }


    // height > 2

    @Test
    fun `arched bridge 4x3`() {
        testArchedBridge(4, 3)
    }

    @Test
    fun `arched bridge 5x3`() {
        testArchedBridge(5, 3)
    }

    @Test
    fun `arched bridge 5x4`() {
        testArchedBridge(5, 4)
    }

    @Test
    fun `arched bridge 6x3`() {
        testArchedBridge(6, 3)
    }

    @Test
    fun `arched bridge 6x6`() {
        testArchedBridge(6, 6)
    }

    @Test
    fun `arched bridge 7x7`() {
        testArchedBridge(7, 7)
    }

    @Test
    fun `arched bridge 11x3`() {
        testArchedBridge(11, 3)
    }


    @Test
    fun `arched bridge 11x11`() {
        testArchedBridge(11, 11)
    }


    @Test
    fun `arched bridge 12x12`() {
        testArchedBridge(12, 12)
    }

    @Test
    fun `arched bridge 17x16`() {
        testArchedBridge(17, 16)
    }

    @Test
    fun `arched bridge 19x16`() {
        testArchedBridge(19, 16)
    }

    private fun testArchedBridge(width: Int, height: Int) {
        val style = Stylesheet().add {
            style("container") {
                size(outWidth.vx, outHeight.vx, 1.vx)
            }
            style("bridge") {
                size(width.vx, height.vx, 1.vx)
                alignX { center() }
            }
        }
        val dom = domRoot {
            node("container") {
                wall("bridge")
            }
        }.buildDom(style, hinting = true)
        build(dom)
        record(out.sliceZ(0))
    }
}