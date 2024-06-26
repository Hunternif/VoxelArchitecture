package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.naturalHeight
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
                y { 50.pct - 3.vx }
            }
        }
        val dom = domRoot {
            room("parent") {
                room("child")
            }
        }.buildDom(style)

        val parent = dom.children[0] as Room
        val child = parent.children[0] as Room

        assertEquals(100.0, parent.naturalHeight, 0.0)
        assertEquals(47.0, child.origin.y, 0.0)
    }

    @Test
    fun `vector offset`() {
        val style = Stylesheet().add {
            style("child") {
                offset(1.vx, 2.vx, 3.vx)
            }
        }
        val dom = domRoot {
            room("child")
        }.buildDom(style)

        val child = dom.query<Room>().first()

        assertEquals(Vec3(1, 2, 3), child.origin)
    }
}