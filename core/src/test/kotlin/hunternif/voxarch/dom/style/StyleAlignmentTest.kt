package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.query
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test

class StyleAlignmentTest {
    @Test
    fun `align non-centered child to non-centered parent room`() {
        testAlignment(Vec3(0, 0, 0)) { }

        testAlignment(Vec3(0.5, 0.0, 0.5)) { alignXZ { center() } }

        testAlignment(Vec3(0, 4, 0)) { alignY { above() } }
        testAlignment(Vec3(0, 1, 0)) { alignY { top() } }
        testAlignment(Vec3(0, -3, 0)) { alignY { below() } }
        testAlignment(Vec3(0, 0, 0)) { alignY { bottom() } }

        testAlignment(Vec3(0.5, 0.0, 0.0)) { alignX { center() } }
        testAlignment(Vec3(1, 0, 0)) { alignX { eastIn() } }
        testAlignment(Vec3(4, 0, 0)) { alignX { eastOut() } }
        testAlignment(Vec3(0, 0, 0)) { alignX { westIn() } }
        testAlignment(Vec3(-3, 0, 0)) { alignX { westOut() } }

        testAlignment(Vec3(0.0, 0.0, 0.5)) { alignZ { center() } }
        testAlignment(Vec3(0, 0, 1)) { alignZ { southIn() } }
        testAlignment(Vec3(0, 0, 4)) { alignZ { southOut() } }
        testAlignment(Vec3(0, 0, 0)) { alignZ { northIn() } }
        testAlignment(Vec3(0, 0, -3)) { alignZ { northOut() } }
    }

    @Test
    fun `align centered child to non-centered parent room`() {
        testAlignment(Vec3(0, 0, 0), centeredChild = true) { }

        testAlignment(Vec3(1.5, 0.0, 1.5), centeredChild = true) { alignXZ { center() } }

        testAlignment(Vec3(0, 4, 0), centeredChild = true) { alignY { above() } }
        testAlignment(Vec3(0, 1, 0), centeredChild = true) { alignY { top() } }
        testAlignment(Vec3(0, -3, 0), centeredChild = true) { alignY { below() } }
        testAlignment(Vec3(0, 0, 0), centeredChild = true) { alignY { bottom() } }

        testAlignment(Vec3(1.5, 0.0, 0.0), centeredChild = true) { alignX { center() } }
        testAlignment(Vec3(2, 0, 0), centeredChild = true) { alignX { eastIn() } }
        testAlignment(Vec3(5, 0, 0), centeredChild = true) { alignX { eastOut() } }
        testAlignment(Vec3(1, 0, 0), centeredChild = true) { alignX { westIn() } }
        testAlignment(Vec3(-2, 0, 0), centeredChild = true) { alignX { westOut() } }

        testAlignment(Vec3(0.0, 0.0, 1.5), centeredChild = true) { alignZ { center() } }
        testAlignment(Vec3(0, 0, 2), centeredChild = true) { alignZ { southIn() } }
        testAlignment(Vec3(0, 0, 5), centeredChild = true) { alignZ { southOut() } }
        testAlignment(Vec3(0, 0, 1), centeredChild = true) { alignZ { northIn() } }
        testAlignment(Vec3(0, 0, -2), centeredChild = true) { alignZ { northOut() } }
    }

    @Test
    fun `align non-centered child to centered parent room`() {
        testAlignment(Vec3(0, 0, 0), centeredParent = true) { }

        testAlignment(Vec3(-1, 0, -1), centeredParent = true) { alignXZ { center() } }

        testAlignment(Vec3(0, 4, 0), centeredParent = true) { alignY { above() } }
        testAlignment(Vec3(0, 1, 0), centeredParent = true) { alignY { top() } }
        testAlignment(Vec3(0, -3, 0), centeredParent = true) { alignY { below() } }
        testAlignment(Vec3(0, 0, 0), centeredParent = true) { alignY { bottom() } }

        testAlignment(Vec3(-1, 0, 0), centeredParent = true) { alignX { center() } }
        testAlignment(Vec3(-0.5, 0.0, 0.0), centeredParent = true) { alignX { eastIn() } }
        testAlignment(Vec3(2.5, 0.0, 0.0), centeredParent = true) { alignX { eastOut() } }
        testAlignment(Vec3(-1.5, 0.0, 0.0), centeredParent = true) { alignX { westIn() } }
        testAlignment(Vec3(-4.5, 0.0, 0.0), centeredParent = true) { alignX { westOut() } }

        testAlignment(Vec3(0, 0, -1), centeredParent = true) { alignZ { center() } }
        testAlignment(Vec3(0.0, 0.0, -0.5), centeredParent = true) { alignZ { southIn() } }
        testAlignment(Vec3(0.0, 0.0, 2.5), centeredParent = true) { alignZ { southOut() } }
        testAlignment(Vec3(0.0, 0.0, -1.5), centeredParent = true) { alignZ { northIn() } }
        testAlignment(Vec3(0.0, 0.0, -4.5), centeredParent = true) { alignZ { northOut() } }
    }

    @Test
    fun `align centered child to centered parent room`() {
        testAlignment(Vec3(0, 0, 0), true, true) { }

        testAlignment(Vec3(0, 0, 0), true, true) { alignXZ { center() } }

        testAlignment(Vec3(0, 4, 0), true, true) { alignY { above() } }
        testAlignment(Vec3(0, 1, 0), true, true) { alignY { top() } }
        testAlignment(Vec3(0, -3, 0), true, true) { alignY { below() } }
        testAlignment(Vec3(0, 0, 0), true, true) { alignY { bottom() } }

        testAlignment(Vec3(0, 0, 0), true, true) { alignX { center() } }
        testAlignment(Vec3(0.5, 0.0, 0.0), true, true) { alignX { eastIn() } }
        testAlignment(Vec3(3.5, 0.0, 0.0), true, true) { alignX { eastOut() } }
        testAlignment(Vec3(-0.5, 0.0, 0.0), true, true) { alignX { westIn() } }
        testAlignment(Vec3(-3.5, 0.0, 0.0), true, true) { alignX { westOut() } }

        testAlignment(Vec3(0, 0, 0), true, true) { alignZ { center() } }
        testAlignment(Vec3(0.0, 0.0, 0.5), true, true) { alignZ { southIn() } }
        testAlignment(Vec3(0.0, 0.0, 3.5), true, true) { alignZ { southOut() } }
        testAlignment(Vec3(0.0, 0.0, -0.5), true, true) { alignZ { northIn() } }
        testAlignment(Vec3(0.0, 0.0, -3.5), true, true) { alignZ { northOut() } }
    }

    private fun testAlignment(
        expectedOrigin: Vec3,
        centeredParent: Boolean = false,
        centeredChild: Boolean = false,
        styleBlock: Rule.() -> Unit,
    ) {
        val style = defaultStyle.add {
            style("parent") {
                size(4.vx, 4.vx, 4.vx)
                snapOrigin { if (centeredParent) floorCenter() else corner() }
            }
            style("child") {
                size(3.vx, 3.vx, 3.vx)
                snapOrigin { if (centeredChild) floorCenter() else corner() }
                styleBlock()
            }
        }
        val dom = domRoot {
            room("parent") {
                room("child")
            }
        }.buildDom(style)
        val child = dom.query<Room>("child").first()

        assertEquals(expectedOrigin, child.origin)
    }
}