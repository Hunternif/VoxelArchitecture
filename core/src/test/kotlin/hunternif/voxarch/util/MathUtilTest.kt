package hunternif.voxarch.util

import hunternif.voxarch.vector.Vec3
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.assertEquals

@RunWith(MockitoJUnitRunner::class)
class MathUtilTest {
    @Test
    fun `symmetric spacing`() {
        assertEquals(listOf<Int>(), symmetricSpacing(2, 3, 4))
        assertEquals(listOf(0, 1, 2), symmetricSpacing(3, 1, 2))
        assertEquals(listOf(0, 2, 4), symmetricSpacing(5, 2, 3))
//        assertEquals(listOf(1, 4), symmetricSpacing(6, 2, 3)) // not implemented
        assertEquals(listOf(0, 5), symmetricSpacing(6, 2, 3))
        assertEquals(listOf(0, 4), symmetricSpacing(5, 3, 4))
        assertEquals(listOf(0, 5), symmetricSpacing(6, 3, 4))
        assertEquals(listOf(0, 4), symmetricSpacing(5, 4, 5))
        assertEquals(listOf(0, 5), symmetricSpacing(6, 4, 5))
    }

    @Test
    fun `rotate Vec3`() {
        assertEquals(Vec3(1, 0, 0), Vec3(0, 0, 1).rotateY(90.0))
        assertEquals(Vec3(0, 3, -1), Vec3(0, 3, 1).rotateY(180.0))
        assertEquals(Vec3(-1, 3, 0), Vec3(0, 3, 1).rotateY(270.0))
        assertEquals(Vec3(0, 0, 1), Vec3(0, 0, 1).rotateY(360.0))
    }
}