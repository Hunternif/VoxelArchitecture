package hunternif.voxarch.magicavoxel

import org.junit.Test

class VoxFileStorageTest {
    @Test
    fun `serialze Vox storage`() {
        val vox = VoxFileStorage(3, 2, 3)
        vox[0, 0, 0] = VoxColor(1)
        vox[0, 0, 1] = VoxColor(2)
        vox[0, 0, 2] = VoxColor(2)
    }
}