package hunternif.voxarch.util

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import hunternif.voxarch.storage.IFixedBlockStorage
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SliceTest {
    @Mock
    lateinit var box: IFixedBlockStorage

    @Before
    fun setup() {
        whenever(box.width) doReturn 1
        whenever(box.length) doReturn 2
        whenever(box.height) doReturn 3
    }

    @Test
    fun `x slice`() {
        val slice = XSlice(box, 0)
        Assert.assertEquals(2, slice.width)
        Assert.assertEquals(3, slice.height)

        slice.getBlock(8, 9)
        verify(box).getBlock(0, 9, 8)
    }

    @Test
    fun `y slice`() {
        val slice = YSlice(box, 0)
        Assert.assertEquals(1, slice.width)
        Assert.assertEquals(2, slice.height)

        slice.getBlock(8, 9)
        verify(box).getBlock(8, 0, 9)
    }

    @Test
    fun `z slice`() {
        val slice = ZSlice(box, 0)
        Assert.assertEquals(1, slice.width)
        Assert.assertEquals(3, slice.height)

        slice.getBlock(8, 9)
        verify(box).getBlock(8, 9, 0)
    }
}