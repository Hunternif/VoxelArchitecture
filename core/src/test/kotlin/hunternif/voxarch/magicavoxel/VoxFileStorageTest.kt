package hunternif.voxarch.magicavoxel

import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.util.assertStorageEquals
import org.junit.Test
import java.nio.file.Paths

class VoxFileStorageTest {
    @Test
    fun `write and read simple vox`() {
        val block = VoxColor(0xffffff)
        val data = ChunkedStorage3D<VoxColor>()
        data[0, 0, 0] = block
        data[1, 1, 1] = block

        val path = Paths.get("./out/voxarch-vox-simple-test.vox")
        data.writeToVoxFile(path)

        val saved = readVoxFile(path)
        assertStorageEquals(data, saved)
    }

    @Test
    fun `write and read vox even size`() {
        val block = VoxColor(0xffffff)
        val data = ChunkedStorage3D<VoxColor>()
        data[0, 0, 0] = block
        data[0, 1, 0] = block
        data[1, 3, -1] = block

        val path = Paths.get("./out/voxarch-vox-even-test.vox")
        data.writeToVoxFile(path)

        val saved = readVoxFile(path)
        assertStorageEquals(data, saved)
    }

    @Test
    fun `write and read vox odd size`() {
        val block = VoxColor(0xffffff)
        val data = ChunkedStorage3D<VoxColor>()
        data[0, 0, 0] = block
        data[0, 1, 0] = block
        data[2, 4, -2] = block

        val path = Paths.get("./out/voxarch-vox-odd-test.vox")
        data.writeToVoxFile(path)

        val saved = readVoxFile(path)
        assertStorageEquals(data, saved)
    }

    @Test
    fun `write and read large multi-chunk vox`() {
        val block = VoxColor(0xffffff)
        val data = ChunkedStorage3D<VoxColor>()
        data[-1, 0, 0] = block
        data[128, 1, 1] = block
        data[300, 1, 1] = block

        val path = Paths.get("./out/voxarch-vox-multichunk-test.vox")
        data.writeToVoxFile(path)

        val saved = readVoxFile(path)
        assertStorageEquals(data, saved)
    }
}