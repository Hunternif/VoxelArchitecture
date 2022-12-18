package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.Room
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
}