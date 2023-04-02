package hunternif.voxarch.dom

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.builder.DomTurretDecor
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.naturalHeight
import hunternif.voxarch.plan.query
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.util.assertNodeTreeEqualsRecursive
import org.junit.Assert.*
import org.junit.Test

class DomTurretDecorTest {
    @Test
    fun `turret adds style rules to itself but not children`() {
        val style = defaultStyle.add {
            styleFor<DomTurretDecor>(DOM_TURRET) {
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

        assertEquals(1.0, roof.naturalHeight, 0.0)
        assertEquals(10.0, room1.naturalHeight, 0.0)
        assertEquals(10.0, room2.naturalHeight, 0.0)
    }

    @Test
    fun `turret and polyRoom with turret decor are identical`() {
        val style = defaultStyle.add {
            style("turret") {
                size(4.vx, 6.vx, 4.vx)
                snapOrigin { floorCenter() }
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

    @Test
    fun `can execute turret decor multiple times`() {
        val style = defaultStyle.add {
            style("turret") {
                size(4.vx, 6.vx, 4.vx)
                roofShape { set(RoofShape.SPIRE_BORDERED) }
            }
        }
        val turret = DomTurretDecor().apply { addStyle("turret") }

        val dom1 = domRoot { addChild(turret) }.buildDom(style)
        val dom2 = domRoot { addChild(turret) }.buildDom(style)

        assertNodeTreeEqualsRecursive(dom1, dom2, testTags = false)
    }
}