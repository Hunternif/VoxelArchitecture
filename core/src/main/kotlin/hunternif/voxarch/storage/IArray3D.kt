package hunternif.voxarch.storage

/** Fixed-size [IStorage3D] that starts at (0, 0, 0) */
interface IArray3D<T>: IStorage3D<T> {
    override val minX: Int get() = 0
    override val maxX: Int get() = length - 1
    override val minY: Int get() = 0
    override val maxY: Int get() = height - 1
    override val minZ: Int get() = 0
    override val maxZ: Int get() = width - 1
    override val length: Int
    override val height: Int
    override val width: Int
}