package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class PathTest : NodeFactory() {
    @Test
    fun `path construction`() {
        val path = newPath(Vec3.ZERO)

        val p0 = Vec3(0, 0, 0)
        path.addPoint(p0)
        assertEquals(0.0, path.totalLength, 0.0)
        assertTrue(path.segments.isEmpty())

        val p1 = Vec3(1, 0, 0)
        path.addPoint(p1)
        assertEquals(1.0, path.totalLength, 0.0)
        assertEquals(1, path.segments.size)
        assertEquals(PathSegment(p0, p1, 1.0, 0.0), path.segments[0])

        val p2 = Vec3(1, 0, 2)
        path.addPoint(p2)
        assertEquals(3.0, path.totalLength, 0.0)
        assertEquals(2, path.segments.size)
        assertEquals(PathSegment(p1, p2, 2.0, 1.0), path.segments[1])
    }

    @Test
    fun `map x to segment`() {
        val path = newPath(Vec3.ZERO,
            Vec3(0, 0, 0), Vec3(1, 0, 0), Vec3(1, 0, 2)
        )

        assertEquals(null, path.mapX(-1.0))
        assertEquals(path.segments[0], path.mapX(0.0))
        assertEquals(path.segments[0], path.mapX(0.5))
        assertEquals(path.segments[1], path.mapX(1.0))
        assertEquals(path.segments[1], path.mapX(1.5))
        assertEquals(path.segments[1], path.mapX(2.0))
        assertEquals(path.segments[1], path.mapX(3.0))
        assertEquals(null, path.mapX(4.0))
    }
}