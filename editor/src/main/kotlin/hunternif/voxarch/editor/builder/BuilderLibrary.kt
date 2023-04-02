package hunternif.voxarch.editor.builder

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.DefaultBuilders


class BuilderLibrary {
    val allBuilders: List<Builder<*>> = listOf(
        DefaultBuilders.Node,
        DefaultBuilders.Room,
        DefaultBuilders.Wall,
        DefaultBuilders.Floor,
        DefaultBuilders.ArchedWindow,
        DefaultBuilders.Foundation,

        DefaultBuilders.CastleCrenelGroundWall,
        DefaultBuilders.CastleCrenelWall,
        DefaultBuilders.CastleCorbel,
        DefaultBuilders.CastleCrenelDecor,
        DefaultBuilders.PyramidRoof,
        DefaultBuilders.TowerTaperedBottom,

        DefaultBuilders.TorchStand,

        DefaultBuilders.Gate,
        DefaultBuilders.Hatch,
    )

    val buildersByNodeType: ListMultimap<Class<*>, Builder<*>> by lazy {
        ArrayListMultimap.create<Class<*>, Builder<*>>().apply {
            allBuilders.forEach {put(it.nodeClass, it) }
        }
    }
}