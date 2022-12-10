package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.Ward
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.PolyShape.ROUND
import hunternif.voxarch.plan.PolyShape.SQUARE
import hunternif.voxarch.plan.query
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class DomWardTest {
    @Test
    fun `square castle ward`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape { set(SQUARE) }
                diameter { 2.vx }
            }
        }
        val dom = domRoot(style) {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom()

        val ward = dom.children[0]
        assertEquals(4, ward.children.size)
        assertEquals(Vec3(1, 0, 1), ward.children[0].origin)
        assertEquals(Vec3(1, 0, -1), ward.children[1].origin)
        assertEquals(Vec3(-1, 0, -1), ward.children[2].origin)
        assertEquals(Vec3(-1, 0, 1), ward.children[3].origin)
    }

    @Test
    fun `round castle ward with 6 edges`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape { set(ROUND) }
                diameter { 10.vx }
                edgeLength { 6.vx }
            }
        }
        val dom = domRoot(style) {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom()

        val ward = dom.children[0]
        assertEquals(6, ward.children.size)
    }

    @Test
    fun `round castle ward with 8 edges`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape { set(ROUND) }
                diameter { 10.vx }
                edgeLength { 4.vx }
            }
        }
        val dom = domRoot(style) {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom()

        val ward = dom.children[0]
        assertEquals(8, ward.children.size)
    }

    @Test
    fun `castle ward with random shape`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape { random(ROUND, SQUARE) }
            }
        }

        val dom1 = domRoot(style, 1).apply {
            ward()
        }.buildDom()
        val ward1 = dom1.query<Ward>().first()
        assertEquals(ROUND, ward1.shape)

        val dom2 = domRoot(style, 2).apply {
            ward()
        }.buildDom()
        val ward2 = dom2.query<Ward>().first()
        assertEquals(SQUARE, ward2.shape)
    }
}