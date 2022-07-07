package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.builder.Ward
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.PolygonShape.POLYGON
import hunternif.voxarch.plan.PolygonShape.SQUARE
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class DomWardTest {
    @Test
    fun `square castle ward`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape = SQUARE
                diameter { 2.vx }
            }
        }
        val dom = DomRoot(style).apply {
            ward {
                allCorners {
                    room()
                }
            }
        }.build()

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
                shape = POLYGON
                diameter { 10.vx }
                edgeLength { 6.vx }
            }
        }
        val dom = DomRoot(style).apply {
            ward {
                allCorners {
                    room()
                }
            }
        }.build()

        val ward = dom.children[0]
        assertEquals(6, ward.children.size)
    }

    @Test
    fun `round castle ward with 8 edges`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape = POLYGON
                diameter { 10.vx }
                edgeLength { 4.vx }
            }
        }
        val dom = DomRoot(style).apply {
            ward {
                allCorners {
                    room()
                }
            }
        }.build()

        val ward = dom.children[0]
        assertEquals(8, ward.children.size)
    }

    @Test
    fun `castle ward with random shape`() {
        val style = Stylesheet().apply {
            styleFor<Ward> {
                shape { random(POLYGON, SQUARE) }
            }
        }
        lateinit var ward: Ward

        DomRoot(style, 1).apply {
            ward { ward = node}
        }.build()
        assertEquals(POLYGON, ward.shape)

        DomRoot(style, 2).apply {
            ward { ward = node}
        }.build()
        assertEquals(SQUARE, ward.shape)
    }
}