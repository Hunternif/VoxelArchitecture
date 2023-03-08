package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.naturalHeight
import hunternif.voxarch.plan.query
import org.junit.Assert.assertEquals
import org.junit.Test

class RuleTest {
    @Test
    fun `rule toString`() {
        val rule = Rule(select("tower").type(Room::class.java)).apply {
            alignY { bottom() }
            offsetY { 15.vx }
            roofShape { randomRoof() }
        }
        assertEquals("""Room .tower {
            |  align y: BOTTOM
            |  offset y: 15
            |  roof shape: random
            |}
        """.trimMargin(), rule.toString())
    }

    @Test
    fun `rule with multiple selectors`() {
        val rule = Rule(
            select("tower").type(Room::class.java),
            select("turret")
        ).apply {
            height { 80.pct }
        }
        assertEquals("""Room .tower, .turret {
            |  height: 80%
            |}
        """.trimMargin(), rule.toString())

        // Apply this rule to a DOM:
        val style = Stylesheet().apply { addRule(rule) }.add {
            style("parent") {
                height { 100.vx }
            }
        }
        val dom = domRoot {
            node("parent") {
                node("child")
                node("tower", "not-room") // not a Room
                room("tower")
                node("turret")
            }
        }.buildDom(style)

        val child = dom.query<Node>("child").first()
        val towerNode = dom.query<Node>("not-room").first()
        val towerRoom = dom.query<Room>("tower").first()
        val turret = dom.query<Node>("turret").first()
        assertEquals(1.0, child.naturalHeight, 0.0)
        assertEquals(1.0, towerNode.naturalHeight, 0.0)
        assertEquals(80.0, towerRoom.naturalHeight, 0.0)
        assertEquals(80.0, turret.naturalHeight, 0.0)
    }
}