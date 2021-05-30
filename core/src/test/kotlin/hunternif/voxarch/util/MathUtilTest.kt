package hunternif.voxarch.util

import hunternif.voxarch.vector.IntVec3
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
        // special case: max < min
        assertEquals(7.0, 6.0.clamp(7.0, 5.0), 0.0)
    }

    @Test
    fun `test roundToEven`() {
        assertEquals(2.0, 1.5.roundToEven(), 0.0)
        assertEquals(2.0, 2.0.roundToEven(), 0.0)
        assertEquals(2.0, 2.1.roundToEven(), 0.0)
        assertEquals(4.0, 3.1.roundToEven(), 0.0)
    }

    @Test
    fun `test next random weighted`() {
        class TestOption(override val probability: Double) : IRandomOption
        val a = TestOption(6.0)
        val b = TestOption(1.0)
        val c = TestOption(3.0)

        val hits = mutableMapOf<TestOption, Int>()
        val rand = Random(3333)

        for (x in 1..10000) {
            val result = rand.nextWeighted(a, b, c)
            hits[result] = (hits[result] ?: 0) + 1
        }

        assertEquals(6000.0, hits[a]?.toDouble() ?: 0.0, 60.0)
        assertEquals(1000.0, hits[b]?.toDouble() ?: 0.0, 60.0)
        assertEquals(3000.0, hits[c]?.toDouble() ?: 0.0, 60.0)
    }

    @Test
    fun `test next random weighted 100%`() {
        class TestOption(override val probability: Double) : IRandomOption
        val a = TestOption(1.0)
        val b = TestOption(0.0)
        val c = TestOption(0.0)

        val hits = mutableMapOf<TestOption, Int>()
        val rand = Random(1)

        for (x in 1..10000) {
            val result = rand.nextWeighted(a, b, c)
            hits[result] = (hits[result] ?: 0) + 1
        }

        assertEquals(10000, hits[a])
        assertEquals(null, hits[b])
        assertEquals(null, hits[c])
    }

    @Test
    fun `test toIntVec3`() {
        assertEquals(IntVec3(1, 2, 3), Vec3(1.0, 2.0, 3.0).toIntVec3())
    }
}