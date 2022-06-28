package hunternif.voxarch.builder

import hunternif.voxarch.plan.Prop
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

class OneBlockPropBuilder(private val material: String) : Builder<Prop>() {

    override fun build(node: Prop, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val block = context.materials.get(material)
        node.origin.apply {
            world.toLocal(trans).setBlock(x, y, z, block)
        }
        super.build(node, trans, world, context)
    }
}