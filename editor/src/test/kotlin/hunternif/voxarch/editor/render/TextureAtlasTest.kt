package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.BaseAppTest
import org.joml.Vector2f
import org.joml.Vector2i
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TextureAtlasTest : BaseAppTest() {
    private val tex1x1 = Texture("1x1")
    private val tex2x1 = Texture("2x1")
    private val tex1x2 = Texture("1x2")
    private val tex2x2 = Texture("2x2")

    @Before
    fun setup() {
        tex1x1.generate(1, 1)
        tex2x1.generate(2, 1)
        tex1x2.generate(1, 2)
        tex2x2.generate(2, 2)
    }

    @Test
    fun `calculate UVs`() {
        val atlas = TextureAtlas(4, 4)
        atlas.init()
        atlas.add(tex1x1)
        val e2 = atlas.add(tex2x2)
        assertEquals(Vector2i(1, 0), e2.start)
        assertEquals(Vector2i(3, 2), e2.end)
        assertEquals(Vector2f(0.25f, 0f), e2.uvStart)
        assertEquals(Vector2f(0.75f, 0.5f), e2.uvEnd)
    }

    @Test
    fun `use padding`() {
        val atlas = TextureAtlas(8, 8, 1)
        atlas.init()
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex1x1)
        val e3 = atlas.add(tex1x1)
        assertEquals(Vector2i(0, 0), e1.startPadded)
        assertEquals(Vector2i(1, 1), e1.start)
        assertEquals(Vector2i(2, 2), e1.end)
        assertEquals(Vector2i(3, 3), e1.endPadded)
        assertEquals(Vector2f(0.125f, 0.125f), e1.uvStart)
        assertEquals(Vector2f(0.25f, 0.25f), e1.uvEnd)
        assertEquals(Vector2i(3, 0), e2.startPadded)
        assertEquals(Vector2i(6, 3), e2.endPadded)
        assertEquals(Vector2i(0, 3), e3.startPadded)
        assertEquals(Vector2i(3, 6), e3.endPadded)
    }

    @Test
    fun `uniform size`() {
        val atlas = TextureAtlas(2, 2)
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex1x1)
        val e3 = atlas.add(tex1x1)
        val e4 = atlas.add(tex1x1)
        assertEquals(Vector2i(0, 0), e1.start)
        assertEquals(Vector2i(1, 1), e1.end)
        assertEquals(Vector2i(1, 0), e2.start)
        assertEquals(Vector2i(2, 1), e2.end)
        assertEquals(Vector2i(0, 1), e3.start)
        assertEquals(Vector2i(1, 2), e3.end)
        assertEquals(Vector2i(1, 1), e4.start)
        assertEquals(Vector2i(2, 2), e4.end)
    }

    @Test
    fun `uneven horizontal size`() {
        val atlas = TextureAtlas(2, 2)
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex2x1)
        assertEquals(Vector2i(0, 0), e1.start)
        assertEquals(Vector2i(1, 1), e1.end)
        assertEquals(Vector2i(0, 1), e2.start)
        assertEquals(Vector2i(2, 2), e2.end)
    }

    @Test
    fun `uneven row height`() {
        val atlas = TextureAtlas(2, 3)
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex1x2)
        val e3 = atlas.add(tex1x1)
        assertEquals(Vector2i(0, 0), e1.start)
        assertEquals(Vector2i(1, 1), e1.end)
        assertEquals(Vector2i(1, 0), e2.start)
        assertEquals(Vector2i(2, 2), e2.end)
        assertEquals(Vector2i(0, 2), e3.start)
        assertEquals(Vector2i(1, 3), e3.end)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `throw if texture doesn't fit`() {
        val atlas = TextureAtlas(2, 2)
        atlas.add(tex1x1)
        atlas.add(tex1x1)
        atlas.add(tex1x2)
    }
}