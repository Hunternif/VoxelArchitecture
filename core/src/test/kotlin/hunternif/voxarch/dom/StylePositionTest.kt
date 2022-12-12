package hunternif.voxarch.dom

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.query
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class StylePositionTest {
    @Test
    fun `vertical offset`() {
        val style = Stylesheet().add {
            style("parent") { height { 100.vx } }
            style("child") {
                height { 50.vx }
                y { 100.pct - 3.vx }
            }
        }
        val dom = domRoot(style) {
            room("parent") {
                room("child")
            }
        }.buildDom()

        val parent = dom.children[0] as Room
        val child = parent.children[0] as Room

        assertEquals(100.0, parent.height, 0.0)
        assertEquals(47.0, child.origin.y, 0.0)
    }

    @Test
    fun `vector offset`() {
        val style = Stylesheet().add {
            style("child") {
                position { base, _ -> base + Vec3(1, 2, 3) }
            }
        }
        val dom = domRoot(style) {
            room("child")
        }.buildDom()

        val child = dom.query<Room>().first()

        assertEquals(Vec3(1, 2, 3), child.origin)
    }
}