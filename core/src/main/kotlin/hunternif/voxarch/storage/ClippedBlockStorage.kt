package hunternif.voxarch.storage

import hunternif.voxarch.builder.fillXYZ
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.IntAABB
import hunternif.voxarch.vector.LinearTransformation

/**
 * Allows modifying storage according to [node]'s clipping mask.
 * Assuming that the node will not be moved or resized.
 */
class ClippedBlockStorage(
    private val storage: IBlockStorage,
    private val node: Node,
) : IBlockStorage {
    /** Node's global AABB */
    private val aabb: IntAABB by lazy { node.findGlobalAABB().toIntAABB() }

    /** Storage that defines the mask. BLocks that are set, are allowed. */
    private val maskStorage: IBlockStorage by lazy {
        val trans = LinearTransformation()
        trans.translate(node.findGlobalPosition())
        trans.rotateY(node.findGlobalRotation())

        val maskStorage = BlockStorageDelegate(ChunkedStorage3D())
        node.fillXYZ(trans) { x, y, z -> maskStorage.setBlock(x, y, z, maskBlock)}
        maskStorage
    }

    override fun getBlock(x: Int, y: Int, z: Int): BlockData? =
        storage.getBlock(x, y, z)

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        if (shouldAllow(x, y, z)) storage.setBlock(x, y, z, block)
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        if (shouldAllow(x, y, z)) storage.clearBlock(x, y, z)
    }

    private fun shouldAllow(x: Int, y: Int, z: Int): Boolean {
        return when (node.clipMask) {
            ClipMask.OFF -> true
            ClipMask.BOX -> aabb.testPoint(x, y, z)
            ClipMask.BOUNDARY -> maskStorage.getBlock(x, y, z) != null
        }
    }

    companion object {
        private val maskBlock = BlockData("_mask_")
    }
}