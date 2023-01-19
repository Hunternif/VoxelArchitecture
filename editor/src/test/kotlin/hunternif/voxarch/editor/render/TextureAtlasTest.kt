package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.BaseAppTest
import org.joml.Vector2f
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TextureAtlasTest : BaseAppTest() {
    private val sheet = Texture("sheet")
    private val tex1x1 = Texture("1x1")
    private val tex2x1 = Texture("2x1")
    private val tex1x2 = Texture("1x2")

    @Before
    fun setup() {
        tex1x1.generate(1, 1)
        tex2x1.generate(2, 1)
        tex1x2.generate(1, 2)
    }

    @Test
    fun `uniform size`() {
        sheet.generate(2, 2)
        val atlas = TextureAtlas(sheet)
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex1x1)
        val e3 = atlas.add(tex1x1)
        val e4 = atlas.add(tex1x1)
        assertEquals(Vector2f(0f, 0f), e1.uvStart)
        assertEquals(Vector2f(1f, 1f), e1.uvEnd)
        assertEquals(Vector2f(1f, 0f), e2.uvStart)
        assertEquals(Vector2f(2f, 1f), e2.uvEnd)
        assertEquals(Vector2f(0f, 1f), e3.uvStart)
        assertEquals(Vector2f(1f, 2f), e3.uvEnd)
        assertEquals(Vector2f(1f, 1f), e4.uvStart)
        assertEquals(Vector2f(2f, 2f), e4.uvEnd)
    }

    @Test
    fun `uneven horizontal size`() {
        sheet.generate(2, 2)
        val atlas = TextureAtlas(sheet)
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex2x1)
        assertEquals(Vector2f(0f, 0f), e1.uvStart)
        assertEquals(Vector2f(1f, 1f), e1.uvEnd)
        assertEquals(Vector2f(0f, 1f), e2.uvStart)
        assertEquals(Vector2f(2f, 2f), e2.uvEnd)
    }

    @Test
    fun `uneven row height`() {
        sheet.generate(2, 3)
        val atlas = TextureAtlas(sheet)
        val e1 = atlas.add(tex1x1)
        val e2 = atlas.add(tex1x2)
        val e3 = atlas.add(tex1x1)
        assertEquals(Vector2f(0f, 0f), e1.uvStart)
        assertEquals(Vector2f(1f, 1f), e1.uvEnd)
        assertEquals(Vector2f(1f, 0f), e2.uvStart)
        assertEquals(Vector2f(2f, 2f), e2.uvEnd)
        assertEquals(Vector2f(0f, 2f), e3.uvStart)
        assertEquals(Vector2f(1f, 3f), e3.uvEnd)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun `throw if texture doesn't fit`() {
        sheet.generate(2, 2)
        val atlas = TextureAtlas(sheet)
        atlas.add(tex1x1)
        atlas.add(tex1x1)
        atlas.add(tex1x2)
    }
}