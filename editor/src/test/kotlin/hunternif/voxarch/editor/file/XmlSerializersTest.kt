package hunternif.voxarch.editor.file

import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class XmlSerializersTest {
    @Test
    fun `serialize Vec3`() {
        assertEquals("<Vec3>(1.0, 2.0, 3.0)</Vec3>",
            serializeToStr(Vec3(1, 2, 3)))
        assertEquals("<Vec3>(-1.1, 2.999999999, 3.0)</Vec3>",
            serializeToStr(Vec3(-1.1, 2.999999999, 3.0)))
    }

    @Test
    fun `deserialize Vec3`() {
        assertEquals(Vec3(1, 2, 3),
            deserialize("<Vec3>(1.0, 2.0, 3.0)</Vec3>", Vec3::class))
        assertEquals(Vec3(-1.1, 2.999999999, 3.0),
            deserialize("<Vec3>(-1.1, 2.999999999, 3.0)</Vec3>", Vec3::class))
    }
}