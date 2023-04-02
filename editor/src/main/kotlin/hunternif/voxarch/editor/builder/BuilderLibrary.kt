package hunternif.voxarch.editor.builder

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.DefaultBuilders


class BuilderLibrary {
    val allBuilders: List<Entry> = listOf(
        Entry("Basic Node", DefaultBuilders.Node),
        Entry("Basic Room", DefaultBuilders.Room),
        Entry("Basic Wall", DefaultBuilders.Wall),
        Entry("Basic Floor", DefaultBuilders.Floor),

        Entry("Arched Window", DefaultBuilders.ArchedWindow),

        Entry("Foundation", DefaultBuilders.Foundation),
        Entry("Castle crenel ground wall", DefaultBuilders.CastleCrenelGroundWall),
        Entry("Castle crenel wall", DefaultBuilders.CastleCrenelWall),
        Entry("Castle crenel decor", DefaultBuilders.CastleCrenelDecor),
        Entry("Castle corbel", DefaultBuilders.CastleCorbel),
        Entry("Spire roof", DefaultBuilders.PyramidRoof),
        Entry("Tapered bottom", DefaultBuilders.TowerTaperedBottom),

        Entry("Torch stand", DefaultBuilders.TorchStand),

        Entry("Basic Gate", DefaultBuilders.Gate),
        Entry("Basic Hatch", DefaultBuilders.Hatch),
    )

    val buildersByInstance: Map<Builder<*>, Entry> by lazy {
        allBuilders.associateBy { it.builder }
    }

    val buildersByNodeType: ListMultimap<Class<*>, Entry> by lazy {
        ArrayListMultimap.create<Class<*>, Entry>().apply {
            allBuilders.forEach { put(it.builder.nodeClass, it) }
        }
    }

    data class Entry(
        val name: String,
        val builder: Builder<*>,
    ) {
        override fun toString(): String = name
    }
}