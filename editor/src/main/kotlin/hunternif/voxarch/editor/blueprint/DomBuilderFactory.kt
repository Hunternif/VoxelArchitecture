package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.builder.*
import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.plan.*

/**
 * This is a static singleton, because the list of DOM builders is
 * part of the language, and it won't change from project to project.
 */
object DomBuilderFactory {
    val allDomBuilders: List<Entry> by lazy {
        listOf(
            Entry("Node") { DomNodeBuilder { Node() } },
            Entry("Room") { DomNodeBuilder { Room() } },
            Entry("PolyRoom") { DomPolyRoomBuilder { PolyRoom() } },
            Entry("Floor") { DomNodeBuilder { Floor() } },
            Entry("Wall") { DomNodeBuilder { Wall() } },
            Entry("Window") { DomNodeBuilder { Window() } },
            Entry("Column") { DomNodeBuilder { Column() } },
            Entry("Arched Bridge") { DomNodeBuilder { Wall() }.addStyle(BLD_ARCHED_BRIDGE) },
            Entry("Arched Window") { DomNodeBuilder { Window() }.addStyle(BLD_ARCHED_WINDOW) },
            Entry("Turret") { DomPolyRoomWithTurretBuilder() },
            Entry("Turret Decor") { DomTurretDecor() },
            Entry("Turret Roof Decor") { DomTurretRoofDecor() },
            Entry("Turret Bottom Decor") { DomTurretBottomDecor() },
            Entry("Extend") { DomExtend() },
            Entry("All Walls") { DomPolySegmentBuilder() },
            Entry("Four Walls") { DomFourWallsBuilder() },
            Entry("Select Walls") { DomSelectWalls() },
            Entry("Random Wall") { DomRandomSegmentBuilder() },
            Entry("All Corners") { DomLogicPolyCornerBuilder() },
            Entry("Four Corners") { DomLogicFourCornerBuilder() },
            Entry("Subdivide") { DomSubdivide() },
            Entry("Repeat") { DomRepeat() },
            Entry("Blueprint") { DomRunBlueprint() },
            Entry("Random") { DomRandom() },
        )
    }

    val domBuildersByName: Map<String, Entry> by lazy {
        allDomBuilders.associateBy { it.name }
    }

    fun create(name: String): DomBuilder? = domBuildersByName[name]?.create?.invoke()

    data class Entry(
        val name: String,
        val create: () -> DomBuilder,
        val clazz: Class<out DomBuilder>,
    ) {
        val className: String = clazz.simpleName
        /** Class of node this builds, if it's a Node builder */
        val nodeClass: Class<out Node>? = (create() as? DomNodeBuilder<*>)?.nodeClass
        val isNodeBuilder: Boolean = nodeClass != null

        companion object {
            inline operator fun <reified T : DomBuilder> invoke(
                name: String,
                noinline create: () -> T,
            ) = Entry(name, create, T::class.java)
        }
    }
}