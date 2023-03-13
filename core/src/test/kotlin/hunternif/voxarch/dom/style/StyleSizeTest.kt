package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.style.property.depth
import hunternif.voxarch.dom.style.property.height
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.style.property.width
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.naturalSize
import hunternif.voxarch.plan.query
import hunternif.voxarch.util.assertVec3Equals
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class StyleSizeTest {
    @Test
    fun `apply size as vector`() {
        testSize(Vec3(1, 2, 3)) {
            size { set(Vec3(1, 2, 3)) }
        }
    }

    @Test
    fun `apply size as absolute individual coordinates`() {
        testSize(Vec3(1, 2, 3)) {
            size(1.vx, 2.vx, 3.vx)
        }
    }

    @Test
    fun `apply size as percentage individual coordinates`() {
        testSize(Vec3(1.0, 2.5, 3.0)) {
            size(10.pct, 25.pct, 30.pct)
        }
    }

    @Test
    fun `apply width before size`() {
        testSize(Vec3(1, 2, 3)) {
            width { 15.vx }
            size(1.vx, 2.vx, 3.vx)
        }
    }

    @Test
    fun `apply width after size`() {
        testSize(Vec3(15, 2, 3)) {
            size(1.vx, 2.vx, 3.vx)
            width { 15.vx }
        }
    }

    private fun testSize(
        expected: Vec3,
        styleBlock: Rule.() -> Unit,
    ) {
        val style = Stylesheet().add {
            style("parent") {
                width { 10.vx }
                height { 10.vx }
                depth { 10.vx }
            }
            style("child") {
                styleBlock()
            }
        }
        val dom = domRoot {
            node("parent") {
                node("child")
            }
        }.buildDom(style)
        val child = dom.query<Node>("child").first()
        assertVec3Equals(expected, child.naturalSize)
    }
}