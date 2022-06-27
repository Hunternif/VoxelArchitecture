package hunternif.voxarch.builder

import hunternif.voxarch.plan.Prop
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.intRoundDown
import hunternif.voxarch.vector.TransformationStack

class OneBlockPropBuilder(private val material: String) : Builder<Prop>() {

    override fun build(node: Prop, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val block = context.materials.get(material)
        val pos = trans.transform(node.origin).intRoundDown()
        world.setBlock(pos, block)
        super.build(node, trans, world, context)
    }
}