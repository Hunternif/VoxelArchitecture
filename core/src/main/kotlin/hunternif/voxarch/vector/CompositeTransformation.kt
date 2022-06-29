package hunternif.voxarch.vector

class CompositeTransformation(
    private vararg val transformations: ITransformation
) : ITransformation {
    override fun transformLocal(vec: Vec3): Vec3 =
        transformations.reversed().fold(vec) { v, t -> t.transformLocal(v) }
}