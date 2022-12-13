package hunternif.voxarch.generator

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.query
import hunternif.voxarch.sandbox.castle.BLD_TOWER_ROOF
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.util.assertNodeTreeEqualsRecursive
import org.junit.Assert.*
import org.junit.Test

class GenTurretDecorTest {
    @Test
    fun `turret adds style rules to itself but not children`() {
        val style = defaultStyle.add {
            styleFor<GenTurretDecor>(DOM_TURRET) {
                // This enables the "roof" element with height 0.0
                roofShape { set(RoofShape.FLAT_BORDERED) }
            }
            styleFor<Room> {
                height { 10.vx }
            }
        }
        val dom = domRoot {
            turret("turret") {
                room(BLD_TOWER_ROOF, "other_room_1")
            }
            room(BLD_TOWER_ROOF, "other_room_2")
        }.buildDom(style)

        val turret = dom.query<Room>("turret").first()
        val roof = turret.query<PolyRoom>(BLD_TOWER_ROOF).first()
        val room1 = dom.query<Room>("other_room_1").first()
        val room2 = dom.query<Room>("other_room_2").first()

        assertEquals(0.0, roof.height, 0.0)
        assertEquals(10.0, room1.height, 0.0)
        assertEquals(10.0, room2.height, 0.0)
    }

    @Test
    fun `turret and polyRoom with turret decor are identical`() {
        val style = defaultStyle.add {
            style("turret") {
                size(4.vx, 6.vx, 4.vx)
            }
        }
        val dom1 = domRoot {
            turret("turret")
        }.buildDom(style)

        val dom2 = domRoot {
            polyRoom("turret") {
                turretDecor()
            }
        }.buildDom(style)

        assertNodeTreeEqualsRecursive(dom1, dom2, testTags = false)
    }
}