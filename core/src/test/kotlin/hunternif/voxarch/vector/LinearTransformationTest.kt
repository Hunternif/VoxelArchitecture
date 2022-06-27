package hunternif.voxarch.vector

import org.junit.Assert.assertEquals
import org.junit.Test

class LinearTransformationTest {
    @Test
    fun testRotate90() {
        val trans = LinearTransformation().rotateY(90.0)
        assertEquals(Vec3(0, 0, 0), trans.transform(0, 0, 0))
        assertEquals(Vec3(1, 2, 0), trans.transform(0, 2, 1))
        assertEquals(Vec3(2, 3, 0), trans.transform(0, 3, 2))
        assertEquals(Vec3(3, -4, -1), trans.transform(1, -4, 3))
    }

    @Test
    fun testRotate180() {
        val trans = LinearTransformation().rotateY(180.0)
        assertEquals(Vec3(0, 0, 0), trans.transform(0, 0, 0))
        assertEquals(Vec3(0, 2, -1), trans.transform(0, 2, 1))
        assertEquals(Vec3(0, 3, -2), trans.transform(0, 3, 2))
        assertEquals(Vec3(-1, -4, -3), trans.transform(1, -4, 3))
    }

    /**
     * From A to B:
     *
     *      A
     *      #
     *      # B
     */
    @Test
    fun testKnightMove() {
        val trans = LinearTransformation()
            .rotateY(-90.0)
            .translate(2, 0, 0)
            .rotateY(90.0)
            .translate(1, 0, 0)
            .translate(0, 99, 0)
        assertEquals(Vec3(1, 99, 2), trans.transform(0, 0, 0))
        assertEquals(Vec3(2, 100, 3), trans.transform(1, 1, 1))
    }

    @Test
    fun testPushAndPopStack() {
        val stack = TransformationStack()
        stack.rotateY(30.0).translate(10.2, -3.4, 0.001)
        val savedAngleY = stack.angleY
        val savedMatrix = stack.matrix.clone()

        stack.apply{
            push()
            translate(-99, 17, 33)
            rotateY(-159.2)
            pop()
        }

        assertEquals(savedAngleY, stack.angleY, 0.0)
        assertEquals(savedMatrix, stack.matrix)
    }
}