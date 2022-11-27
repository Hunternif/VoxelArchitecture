package hunternif.voxarch.generator

import hunternif.voxarch.dom.DOM_TURRET
import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.turret
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.query
import hunternif.voxarch.sandbox.castle.BLD_TOWER_ROOF
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import org.junit.Assert.*
import org.junit.Test

class TurretGeneratorTest {
    @Test
    fun `turret adds style rules to itself but not children`() {
        val style = defaultStyle.apply {
            styleForGen<TurretGenerator>(DOM_TURRET) {
                // This enables the "roof" element with height 0.0
                roofShape = RoofShape.FLAT_BORDERED
            }
            style2For<Room> {
                height2 { 10.vx }
            }
        }
        val dom = DomRoot(style).apply {
            turret("turret") {
                room(BLD_TOWER_ROOF, "other_room_1")
            }
            room(BLD_TOWER_ROOF, "other_room_2")
        }.buildDom()

        val turret = dom.query<Room>("turret").first()
        val roof = turret.query<PolygonRoom>(BLD_TOWER_ROOF).first()
        val room1 = dom.query<Room>("other_room_1").first()
        val room2 = dom.query<Room>("other_room_2").first()

        assertEquals(0.0, roof.height, 0.0)
        assertEquals(10.0, room1.height, 0.0)
        assertEquals(10.0, room2.height, 0.0)
    }
}