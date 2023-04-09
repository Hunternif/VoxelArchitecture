package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.Ward
import hunternif.voxarch.dom.builder.countChordsGivenLength
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.plan.PolyShape.*
import hunternif.voxarch.plan.query
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.sqrt

class DomWardTest {
    @Test
    fun `square castle ward`() {
        val style = defaultStyle.add {
            styleFor<Ward> {
                shape { set(SQUARE) }
                diameter { 3.vx }
                snapOrigin { floorCenter() }
            }
        }
        val dom = domRoot {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom(style)

        val ward = dom.children[0]
        assertEquals(4, ward.children.size)
        assertEquals(Vec3(1, 0, 1), ward.children[0].origin)
        assertEquals(Vec3(1, 0, -1), ward.children[1].origin)
        assertEquals(Vec3(-1, 0, -1), ward.children[2].origin)
        assertEquals(Vec3(-1, 0, 1), ward.children[3].origin)
    }

    @Test
    fun `round castle ward with 6 edges`() {
        val style = Stylesheet().add {
            styleFor<Ward> {
                shape { set(ROUND) }
                diameter { 11.vx }
                sideCount { set(6) }
            }
        }
        val dom = domRoot {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom(style)

        val ward = dom.children[0]
        assertEquals(6, ward.children.size)
    }

    @Test
    fun `round castle ward with 8 edges`() {
        val style = Stylesheet().add {
            styleFor<Ward> {
                shape { set(ROUND) }
                diameter { 11.vx }
                sideCount { set(8) }
            }
        }
        val dom = domRoot {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom(style)

        val ward = dom.children[0]
        assertEquals(8, ward.children.size)
    }

    @Test
    fun `round castle ward with 8 edges via octagon`() {
        val style = Stylesheet().add {
            styleFor<Ward> {
                shape { set(OCTAGON) }
                diameter { 11.vx }
            }
        }
        val dom = domRoot {
            ward {
                allCorners {
                    room()
                }
            }
        }.buildDom(style)

        val ward = dom.children[0]
        assertEquals(8, ward.children.size)
    }

    @Test
    fun `castle ward with random shape`() {
        val style = Stylesheet().add {
            styleFor<Ward> {
                shape { random(ROUND, SQUARE) }
            }
        }

        val dom1 = domRoot {
            ward()
        }.buildDom(style, 1)
        val ward1 = dom1.query<Ward>().first()
        assertEquals(ROUND, ward1.shape)

        val dom2 = domRoot {
            ward()
        }.buildDom(style, 2)
        val ward2 = dom2.query<Ward>().first()
        assertEquals(SQUARE, ward2.shape)
    }

    @Test
    fun `count chords given length`() {
        assertEquals(6, countChordsGivenLength(1.0, 1.0))
        assertEquals(4, countChordsGivenLength(sqrt(2.0), 1.0))
        assertEquals(3, countChordsGivenLength(sqrt(3.0), 1.0))
        assertEquals(3, countChordsGivenLength(sqrt(3.0) - 0.1, 1.0))
        assertEquals(3, countChordsGivenLength(sqrt(3.0) + 0.1, 1.0))
        assertEquals(2, countChordsGivenLength(2.0, 1.0))
        assertEquals(0, countChordsGivenLength(99.0, 1.0))

//        // Gauging which algorithm makes more sense:
//        for (r in 2..8) {
//            val countFromEdgeLength = countChordsGivenLength(4.0, r.toDouble()).clampMin(4).roundToEven()
//            val countFromCircle = (ceil(r.toDouble() * 0.334).toInt() * 4)
//            println("radius $r:  $countFromEdgeLength  $countFromCircle")
//        }
    }
}