package hunternif.voxarch.util

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
}