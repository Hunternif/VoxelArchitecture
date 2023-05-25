package hunternif.voxarch.editor.builder

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.DefaultBuilders
import hunternif.voxarch.plan.Node


class BuilderLibrary {
    val allBuilders: List<Entry> = listOf(
        Entry("Basic Node", DefaultBuilders.Node),
        Entry("Basic Room", DefaultBuilders.Room),
        Entry("Basic Wall", DefaultBuilders.Wall),
        Entry("Basic Floor", DefaultBuilders.Floor),
        Entry("Basic Column", DefaultBuilders.Column),
        Entry("Basic Stairs", DefaultBuilders.Stairs),
        Entry("Thin Stairs", DefaultBuilders.ThinStairs),
        Entry("Fill", DefaultBuilders.Fill),

        Entry("Arched Window", DefaultBuilders.ArchedWindow),
        Entry("Arched Bridge", DefaultBuilders.ArchedBridge),

        Entry("Foundation", DefaultBuilders.Foundation),
        Entry("Castle crenel ground wall", DefaultBuilders.CastleCrenelGroundWall),
        Entry("Castle crenel wall", DefaultBuilders.CastleCrenelWall),
        Entry("Castle crenel path", DefaultBuilders.CastleCrenelPath),
        Entry("Castle crenel decor", DefaultBuilders.CastleCrenelDecor),
        Entry("Castle corbel", DefaultBuilders.CastleCorbel),
        Entry("Spire roof", DefaultBuilders.PyramidRoof),
        Entry("Tapered bottom", DefaultBuilders.TowerTaperedBottom),

        Entry("Torch stand", DefaultBuilders.TorchStand),
        Entry("Wall with torches", DefaultBuilders.WallWithTorches),

        Entry("Basic Gate", DefaultBuilders.Gate),
        Entry("Basic Hatch", DefaultBuilders.Hatch),

        Entry("Snake path", DefaultBuilders.SnakePath),
        Entry("One block", DefaultBuilders.OneBlock),
    )

    val buildersByInstance: Map<Builder<*>, Entry> by lazy {
        allBuilders.associateBy { it.builder }
    }

    val buildersByName: Map<String, Entry> by lazy {
        allBuilders.associateBy { it.name }
    }

    val buildersByNodeType: ListMultimap<Class<*>, Entry> by lazy {
        ArrayListMultimap.create<Class<*>, Entry>().apply {
            allBuilders.forEach { put(it.builder.nodeClass, it) }
        }
    }

    /** Returns all builders with matching type for a given node */
    fun findBuildersFor(node: Node): List<Entry> {
        val result = mutableListOf<Entry>()
        var nodeClass: Class<*> = node::class.java
        while (Node::class.java.isAssignableFrom(nodeClass)) {
            result.addAll(buildersByNodeType[nodeClass])
            nodeClass = nodeClass.superclass
        }
        return result
    }

    data class Entry(
        val name: String,
        val builder: Builder<*>,
    ) {
        init {
            builder.name = name
        }
        override fun toString(): String = name
    }
}