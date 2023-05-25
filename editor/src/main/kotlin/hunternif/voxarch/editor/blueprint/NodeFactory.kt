package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.plan.*

/**
 * This is a static singleton, because the list of Node types is
 * part of the language, and it won't change from project to project.
 */
object NodeFactory {
    val allNodeTypes: List<Entry> by lazy {
        listOf(
            Entry("Node") { Node() },
            Entry("Room") { Room() },
            Entry("PolyRoom") { PolyRoom() },
            Entry("Floor") { Floor() },
            Entry("Wall") { Wall() },
            Entry("Window") { Window() },
            Entry("Column") { Column() },
            Entry("Staircase") { Staircase() },
        )
    }

    val nodeTypesByName: Map<String, Entry> by lazy {
        allNodeTypes.associateBy { it.name }
    }

    fun create(name: String): Node? = nodeTypesByName[name]?.create?.invoke()

    data class Entry(
        val name: String,
        val create: () -> Node,
        val nodeClass: Class<out Node>,
    ) {
        companion object {
            inline operator fun <reified T : Node> invoke(
                name: String,
                noinline create: () -> T,
            ) = Entry(name, create, T::class.java)
        }
    }
}