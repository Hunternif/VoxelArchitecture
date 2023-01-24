package hunternif.voxarch.dom

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.junit.Assert
import org.junit.Test

class DomCornerTest {
    @Test
    fun `square polygon room`() {
        val style = Stylesheet().add {
            styleFor<PolyRoom> {
                shape { set(PolyShape.SQUARE) }
                diameter { 2.vx }
            }
        }
        val dom = domRoot {
            polyRoom {
                allCorners {
                    room()
                }
            }
        }.buildDom(style)

        val room = dom.children[0]
        Assert.assertEquals(4, room.children.size)
        Assert.assertEquals(Vec3(1, 0, 1), room.children[0].origin)
        Assert.assertEquals(Vec3(1, 0, -1), room.children[1].origin)
        Assert.assertEquals(Vec3(-1, 0, -1), room.children[2].origin)
        Assert.assertEquals(Vec3(-1, 0, 1), room.children[3].origin)
    }

    @Test
    fun `rectangle room`() {
        val style = Stylesheet().add {
            styleFor<Room> {
                width { 2.vx }
                depth { 4.vx }
            }
        }
        val dom = domRoot {
            room {
                fourCorners {
                    room()
                }
            }
        }.buildDom(style)

        val room = dom.children[0]
        Assert.assertEquals(4, room.children.size)
        Assert.assertEquals(Vec3(1, 0, 2), room.children[0].origin)
        Assert.assertEquals(Vec3(1, 0, -2), room.children[1].origin)
        Assert.assertEquals(Vec3(-1, 0, -2), room.children[2].origin)
        Assert.assertEquals(Vec3(-1, 0, 2), room.children[3].origin)
    }
}