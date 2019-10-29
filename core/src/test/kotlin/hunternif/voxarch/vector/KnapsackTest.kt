package hunternif.voxarch.vector

import hunternif.voxarch.world.clockwise
import hunternif.voxarch.world.clockwiseInnerPerimeter
import hunternif.voxarch.world.fitBox
import hunternif.voxarch.world.outerPerimeter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KnapsackTest {
    @Test
    fun `fit simple square`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(0, 1),
            IntVec2(1, 1)
        )
        val box = area.fitBox()
        assertEquals(IntVec2(0, 0), box.start)
        assertEquals(IntVec2(1, 1), box.end)
    }

    @Test
    fun `fit rectangle`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(0, 1),
            IntVec2(1, 1),
            IntVec2(0, 2),
            IntVec2(1, 2),
            IntVec2(1, 3)
        )
        val box = area.fitBox()
        assertEquals(IntVec2(0, 0), box.start)
        assertEquals(IntVec2(1, 2), box.end)
    }

    @Test
    fun `fit cross`() {
        val area = hashSetOf(
            IntVec2(-1, -1),
            IntVec2(-1, 0),
            IntVec2(-1, 1),
            IntVec2(0, -1),
            IntVec2(0, 0),
            IntVec2(0, 1),
            IntVec2(1, -1),
            IntVec2(1, 0),
            IntVec2(1, 1),
            IntVec2(0, 2),
            IntVec2(0, -2),
            IntVec2(2, 0),
            IntVec2(-2, 0)
        )
        val box = area.fitBox()
        assertEquals(IntVec2(-1, -1), box.start)
        assertEquals(IntVec2(1, 1), box.end)
    }

    @Test
    fun `outer perimeter`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(2, 1)
        )
        val perimeter = area.outerPerimeter()
        assertEquals(8, perimeter.count())
        assertTrue(perimeter.contains(IntVec2(0, -1)))
        assertTrue(perimeter.contains(IntVec2(1, -1)))
        assertTrue(perimeter.contains(IntVec2(2, 0)))
        assertTrue(perimeter.contains(IntVec2(3, 1)))
        assertTrue(perimeter.contains(IntVec2(2, 2)))
        assertTrue(perimeter.contains(IntVec2(1, 1)))
        assertTrue(perimeter.contains(IntVec2(0, 1)))
        assertTrue(perimeter.contains(IntVec2(-1, 0)))
    }

    @Test
    fun `clockwise walk outer perimeter of a tiny squiggle`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(2, 1)
        )
        val perimeter = area.outerPerimeter()
        val walk = perimeter.clockwise(IntVec2(0, -1)).toList()
        assertEquals(8, walk.count())
        assertEquals(IntVec2(0, -1), walk[0])
        assertEquals(IntVec2(1, -1), walk[1])
        assertEquals(IntVec2(2, 0), walk[2])
        assertEquals(IntVec2(3, 1), walk[3])
        assertEquals(IntVec2(2, 2), walk[4])
        assertEquals(IntVec2(1, 1), walk[5])
        assertEquals(IntVec2(0, 1), walk[6])
        assertEquals(IntVec2(-1, 0), walk[7])
    }

    @Test
    fun `clockwise walk inner perimeter of a square`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(0, 1),
            IntVec2(1, 1)
        )
        val walk = area.clockwiseInnerPerimeter(IntVec2(0, 0)).toList()
        assertEquals(4, walk.count())
        assertEquals(IntVec2(0, 0), walk[0])
        assertEquals(IntVec2(1, 0), walk[1])
        assertEquals(IntVec2(1, 1), walk[2])
        assertEquals(IntVec2(0, 1), walk[3])
    }

    @Test
    fun `clockwise walk inner perimeter of a square from a different point`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(0, 1),
            IntVec2(1, 1)
        )
        val walk = area.clockwiseInnerPerimeter(IntVec2(0, 1)).toList()
        assertEquals(4, walk.count())
        assertEquals(IntVec2(0, 1), walk[0])
        assertEquals(IntVec2(0, 0), walk[1])
        assertEquals(IntVec2(1, 0), walk[2])
        assertEquals(IntVec2(1, 1), walk[3])
    }

    @Test
    fun `clockwise walk inner perimeter of a bigger rectangle`() {
        val area = hashSetOf(
            IntVec2(0, 0),
            IntVec2(1, 0),
            IntVec2(2, 0),
            IntVec2(3, 0),
            IntVec2(0, 1),
            IntVec2(1, 1),
            IntVec2(2, 1),
            IntVec2(3, 1),
            IntVec2(0, 2),
            IntVec2(1, 2),
            IntVec2(2, 2),
            IntVec2(3, 2)
        )
        val walk = area.clockwiseInnerPerimeter(IntVec2(0, 0)).toList()
        assertEquals(10, walk.count())
        assertEquals(IntVec2(0, 0), walk[0])
        assertEquals(IntVec2(1, 0), walk[1])
        assertEquals(IntVec2(2, 0), walk[2])
        assertEquals(IntVec2(3, 0), walk[3])
        assertEquals(IntVec2(3, 1), walk[4])
        assertEquals(IntVec2(3, 2), walk[5])
        assertEquals(IntVec2(2, 2), walk[6])
        assertEquals(IntVec2(1, 2), walk[7])
        assertEquals(IntVec2(0, 2), walk[8])
        assertEquals(IntVec2(0, 1), walk[9])
    }
}