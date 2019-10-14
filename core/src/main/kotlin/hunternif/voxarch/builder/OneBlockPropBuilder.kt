package hunternif.voxarch.builder

import hunternif.voxarch.plan.Prop
import hunternif.voxarch.storage.IBlockStorage

class OneBlockPropBuilder(private val material: String) : Builder<Prop>() {

    override fun build(node: Prop, world: IBlockStorage, context: BuildContext) {
        val block = context.materials.get(material)
        node.origin.apply {
            world.setBlock(x.toInt(), y.toInt(), z.toInt(), block)
        }
        super.build(node, world, context)
    }
}