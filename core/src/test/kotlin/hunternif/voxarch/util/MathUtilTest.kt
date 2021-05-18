package hunternif.voxarch.util

import hunternif.voxarch.vector.Vec3
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.assertEquals
import kotlin.random.Random

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
        assertEquals(Vec3(-1, 3, 0), Vec3(0, 3, 1).rotateY(-90.0))
        assertEquals(Vec3(0, 3, -1), Vec3(0, 3, 1).rotateY(-180.0))
        assertEquals(Vec3(1, 0, 0), Vec3(0, 0, 1).rotateY(-270.0))
        assertEquals(Vec3(0, 0, 1), Vec3(0, 0, 1).rotateY(-360.0))
    }

    @Test
    fun `random even int via range arg`() {
        // seed 333 returns all values from 1..10 within 10 calls
        val rand = Random(333)
        val validResults = listOf(2, 4, 6, 8, 10)
        for (x in 1..10) {
            val result = rand.nextEvenInt(1 .. 10)
            assert(validResults.contains(result)) { "Returned $result" }
        }
    }

    @Test
    fun `random even int via int arg`() {
        // seed 333 returns all values from 1..10 within 10 calls
        val rand = Random(333)
        val validResults = listOf(2, 4, 6, 8, 10)
        for (x in 1..10) {
            val result = rand.nextEvenInt(1, 11)
            assert(validResults.contains(result)) { "Returned $result" }
        }
    }

    @Test
    fun `test clamp`() {
        assertEquals(2.0, 1.0.clamp(2.0, 5.0), 0.0)
        assertEquals(2.0, 2.0.clamp(2.0, 5.0), 0.0)
        assertEquals(3.0, 3.0.clamp(2.0, 5.0), 0.0)
        assertEquals(5.0, 5.0.clamp(2.0, 5.0), 0.0)
        assertEquals(5.0, 6.0.clamp(2.0, 5.0), 0.0)
    }

    @Test
    fun `test roundToEven`() {
        assertEquals(2.0, 1.5.roundToEven(), 0.0)
        assertEquals(2.0, 2.0.roundToEven(), 0.0)
        assertEquals(2.0, 2.1.roundToEven(), 0.0)
        assertEquals(4.0, 3.1.roundToEven(), 0.0)
    }
}