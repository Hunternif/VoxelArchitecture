package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test

class DomWallTest {
    @Test
    fun `create walls on square polygon`() {
        val style = defaultStyle.add {
            styleFor<PolyRoom> {
                shape { set(PolyShape.SQUARE) }
                diameter { 2.vx }
            }
        }
        val seedCounter = DomBuilderSeedCounter()
        val dom = domRoot {
            polyRoom {
                allWalls {
                    addChild(seedCounter)
                }
            }
        }.buildDom(style)

        val room = dom.children[0]
        assertEquals(4, room.children.size)

        val w1 = room.children[0] as Wall
        val w2 = room.children[1] as Wall
        val w3 = room.children[2] as Wall
        val w4 = room.children[3] as Wall
        assertEquals(Vec3(1, 0, 1), w1.origin)
        assertEquals(Vec3(1, 0, -1), w1.end)
        assertEquals(Vec3(1, 0, -1), w2.origin)
        assertEquals(Vec3(-1, 0, -1), w2.end)
        assertEquals(Vec3(-1, 0, -1), w3.origin)
        assertEquals(Vec3(-1, 0, 1), w3.end)
        assertEquals(Vec3(-1, 0, 1), w4.origin)
        assertEquals(Vec3(1, 0, 1), w4.end)

        assertEquals(listOf(20003L, 20004L, 20005L, 20006L), seedCounter.seeds)
    }

    @Test
    fun `create 4 walls on rectangle room`() {
        val style = defaultStyle.add {
            styleFor<Room> {
                width { 2.vx }
                depth { 4.vx }
            }
        }
        val seedCounter = DomBuilderSeedCounter()
        val dom = domRoot {
            room {
                fourWalls {
                    addChild(seedCounter)
                }
            }
        }.buildDom(style)

        verifyFourWalls(dom)
        assertEquals(listOf(20003L, 20004L, 20005L, 20006L), seedCounter.seeds)
    }

    @Test
    fun `create all walls on rectangle room`() {
        val style = defaultStyle.add {
            styleFor<Room> {
                width { 2.vx }
                depth { 4.vx }
            }
        }
        val seedCounter = DomBuilderSeedCounter()
        val dom = domRoot {
            room {
                allWalls {
                    addChild(seedCounter)
                }
            }
        }.buildDom(style)

        verifyFourWalls(dom)
        assertEquals(listOf(20003L, 20004L, 20005L, 20006L), seedCounter.seeds)
    }

    private fun verifyFourWalls(dom: Node) {
        val room = dom.children[0]
        assertEquals(4, room.children.size)

        val w1 = room.children[0] as Wall
        val w2 = room.children[1] as Wall
        val w3 = room.children[2] as Wall
        val w4 = room.children[3] as Wall
        assertEquals(Vec3(1, 0, 2), w1.origin)
        assertEquals(Vec3(1, 0, -2), w1.end)
        assertEquals(Vec3(1, 0, -2), w2.origin)
        assertEquals(Vec3(-1, 0, -2), w2.end)
        assertEquals(Vec3(-1, 0, -2), w3.origin)
        assertEquals(Vec3(-1, 0, 2), w3.end)
        assertEquals(Vec3(-1, 0, 2), w4.origin)
        assertEquals(Vec3(1, 0, 2), w4.end)
    }

    private class DomBuilderSeedCounter : DomBuilder() {
        val seeds = mutableListOf<Long>()
        override fun postLayout(element: StyledElement<*>) {
            seeds.add(element.parent?.seed ?: 0L)
        }
    }
}