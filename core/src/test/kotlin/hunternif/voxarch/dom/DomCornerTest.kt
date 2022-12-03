package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.junit.Assert
import org.junit.Test

class DomCornerTest {
    @Test
    fun `square polygon room`() {
        val style = Stylesheet().apply {
            styleFor<PolygonRoom> {
                shape { set(PolygonShape.SQUARE) }
                diameter { 2.vx }
            }
        }
        val dom = DomRoot(style).apply {
            polygonRoom {
                allCorners {
                    room()
                }
            }
        }.buildDom()

        val room = dom.children[0]
        Assert.assertEquals(4, room.children.size)
        Assert.assertEquals(Vec3(1, 0, 1), room.children[0].origin)
        Assert.assertEquals(Vec3(1, 0, -1), room.children[1].origin)
        Assert.assertEquals(Vec3(-1, 0, -1), room.children[2].origin)
        Assert.assertEquals(Vec3(-1, 0, 1), room.children[3].origin)
    }

    @Test
    fun `rectangle room`() {
        val style = Stylesheet().apply {
            styleFor<Room> {
                width { 2.vx }
                length { 4.vx }
            }
        }
        val dom = DomRoot(style).apply {
            room {
                fourCorners {
                    room()
                }
            }
        }.buildDom()

        val room = dom.children[0]
        Assert.assertEquals(4, room.children.size)
        Assert.assertEquals(Vec3(1, 0, 2), room.children[0].origin)
        Assert.assertEquals(Vec3(1, 0, -2), room.children[1].origin)
        Assert.assertEquals(Vec3(-1, 0, -2), room.children[2].origin)
        Assert.assertEquals(Vec3(-1, 0, 2), room.children[3].origin)
    }
}