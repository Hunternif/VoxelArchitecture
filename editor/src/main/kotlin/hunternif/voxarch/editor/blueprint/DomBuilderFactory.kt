package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.builder.BLD_ARCHED_BRIDGE
import hunternif.voxarch.builder.BLD_ARCHED_WINDOW
import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Archway
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.ArrowsAlt
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.BorderNone
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.CaretDown
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.ChessRook
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Code
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.EllipsisH
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.ICursor
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Square
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Star
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Stop
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.WindowMinimize
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
            Entry("PolyRoom", Star) { DomPolyRoomBuilder { PolyRoom() } },
            Entry("Floor", WindowMinimize) { DomNodeBuilder { Floor() } },
            Entry("Wall", Stop) { DomNodeBuilder { Wall() } },
            Entry("Window") { DomNodeBuilder { Window() } },
            Entry("Column", ICursor) { DomNodeBuilder { Column() } },
            Entry("Arched Bridge", Archway) { DomNodeBuilder { Wall() }.addStyle(BLD_ARCHED_BRIDGE) },
            Entry("Arched Window", Archway) { DomNodeBuilder { Window() }.addStyle(BLD_ARCHED_WINDOW) },
            Entry("Turret", ChessRook) { DomPolyRoomWithTurretBuilder() },
            Entry("Turret Decor", ChessRook) { DomTurretDecor() },
            Entry("Turret Roof Decor", ChessRook) { DomTurretRoofDecor() },
            Entry("Turret Bottom Decor", CaretDown) { DomTurretBottomDecor() },
            Entry("Extend", ArrowsAlt) { DomExtend() },
            Entry("All Walls", Star) { DomPolySegmentBuilder() },
            Entry("Four Walls", Square) { DomFourWallsBuilder() },
            Entry("Select Walls") { DomSelectWalls() },
            Entry("Random Wall") { DomRandomSegmentBuilder() },
            Entry("All Corners", "..") { DomLogicPolyCornerBuilder() },
            Entry("Four Corners", "::") { DomLogicFourCornerBuilder() },
            Entry("Subdivide", BorderNone) { DomSubdivide() },
            Entry("Repeat", EllipsisH) { DomRepeat() },
            Entry("Blueprint", Code) { DomRunBlueprint() },
            Entry("Random", ) { DomRandom() },
        )
    }

    val domBuildersByName: Map<String, Entry> by lazy {
        allDomBuilders.associateBy { it.name }
    }

    fun create(name: String): DomBuilder? = domBuildersByName[name]?.create?.invoke()

    data class Entry(
        val name: String,
        val icon: String,
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
                icon: String = "",
                noinline create: () -> T,
            ) = Entry(name, icon, create, T::class.java)
        }
    }
}