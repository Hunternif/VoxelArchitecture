package hunternif.voxarch.world

import hunternif.voxarch.util.Direction.*
import hunternif.voxarch.vector.Array2D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.Segment.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MountainDetectorTest {
    @Test
    fun slope() {
        val map = HeightMap(3, 1)
        map[0, 0] = 0
        map[1, 0] = 1
        map[2, 0] = 3

        assertEquals(1.0, map.midSlope(IntVec2(0, 0), EAST), 0.0)
        assertEquals(1.5, map.midSlope(IntVec2(1, 0), EAST), 0.0)
        assertEquals(2.0, map.midSlope(IntVec2(2, 0), EAST), 0.0)
    }

    @Test
    fun gradient() {
        val map = HeightMap(listOf(
            listOf(0, 0, 0),
            listOf(1, 0, 0),
            listOf(0, 0, 1)
        ))

        val gradient = map.gradient()

        assertEquals(Slope(SOUTH, 1.0), gradient[0, 0])
        assertEquals(Slope(EAST, 0.0), gradient[1, 0])
        assertEquals(Slope(EAST, 0.0), gradient[2, 0])
        assertEquals(Slope(WEST, 1.0), gradient[0, 1])
        assertEquals(Slope(WEST, 0.5), gradient[1, 1])
        assertEquals(Slope(SOUTH, 0.5), gradient[2, 1])
        assertEquals(Slope(NORTH, 1.0), gradient[0, 2])
        assertEquals(Slope(EAST, 0.5), gradient[1, 2])
        assertEquals(Slope(EAST, 1.0), gradient[2, 2])
    }

    @Test
    fun segments() {
        val map = HeightMap(listOf(
            listOf(0, 0, 0),
            listOf(0, 1, 2),
            listOf(0, 2, 2)
        ))

        val segments = map.segments(
            MountainDetectorConfig(1.5, 1.5)
        )

        assertEquals(GROUND, segments[0, 0])
        assertEquals(GROUND, segments[1, 0])
        assertEquals(SLOPE, segments[2, 0])
        assertEquals(GROUND, segments[0, 1])
        assertEquals(GROUND, segments[1, 1])
        assertEquals(TOP, segments[2, 1])
        assertEquals(SLOPE, segments[0, 2])
        assertEquals(TOP, segments[1, 2])
        assertEquals(TOP, segments[2, 2])
    }

    @Test
    fun cluster() {
        val map = HeightMap(12, 12)
        val points = setOf(
            IntVec2(10, 10),
            IntVec2(0, 0),
            IntVec2(5, 5),
            IntVec2(0, 1),
            IntVec2(10, 11),
            IntVec2(1, 1)
        )

        val clusters = map.cluster(points, MountainDetectorConfig())

        assertEquals(3, clusters.size)
        val sorted = clusters.sortedBy { it.size }
        val cluster1 = sorted[0]
        val cluster2 = sorted[1]
        val cluster3 = sorted[2]

        assertTrue(cluster1.contains(IntVec2(5, 5)))
        assertTrue(cluster2.contains(IntVec2(10, 10)))
        assertTrue(cluster2.contains(IntVec2(10, 11)))
        assertTrue(cluster3.contains(IntVec2(0, 0)))
        assertTrue(cluster3.contains(IntVec2(0, 1)))
        assertTrue(cluster3.contains(IntVec2(1, 1)))
    }

    @Test
    fun cluster2() {
        val map = HeightMap(5, 5)
        val points = setOf(
            IntVec2(1, 3),
            IntVec2(0, 3),
            IntVec2(4, 0)
        )

        val clusters = map.cluster(points, MountainDetectorConfig())

        assertEquals(2, clusters.size)
    }

    @Test
    fun `separate mountains in descendFromTop`() {
        val map = HeightMap(listOf(
            listOf(0, 1),
            listOf(1, 0)
        ))
        val segments = Array2D(listOf(
            listOf(SLOPE, TOP),
            listOf(TOP, SLOPE)
        ))

        val m1 = map.descendFromTop(setOf(IntVec2(1, 0)), segments)
        assertEquals(2, m1.slope.size)
        assertTrue(m1.slope.contains(IntVec2(0,0)))
        assertTrue(m1.slope.contains(IntVec2(1,1)))

        val m2 = map.descendFromTop(setOf(IntVec2(0, 1)), segments)
        assertEquals(2, m2.slope.size)
        assertTrue(m2.slope.contains(IntVec2(0,0)))
        assertTrue(m2.slope.contains(IntVec2(1,1)))
    }
}