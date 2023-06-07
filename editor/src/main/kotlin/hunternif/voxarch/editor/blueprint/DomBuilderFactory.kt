package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.builder.BLD_ARCHED_BRIDGE
import hunternif.voxarch.builder.BLD_ARCHED_WINDOW
import hunternif.voxarch.builder.BLD_SPACE
import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.editor.blueprint.DomBuilderFactory.Group.*
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Archway
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.ArrowsAlt
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.BorderNone
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Campground
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.CaretDown
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.CaretRight
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.ChessRook
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Code
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.EllipsisH
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.ICursor
import hunternif.voxarch.editor.gui.FontAwesomeIcons.Companion.Signal
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
            Entry(NODE, "Node") { DomNodeBuilder { Node() } },
            Entry(NODE, "Space") { DomNodeBuilder { Node() }.addStyle(BLD_SPACE) },
            Entry(NODE, "Room") { DomNodeBuilder { Room() } },
            Entry(NODE, "PolyRoom", Star) { DomPolyRoomBuilder { PolyRoom() } },
            Entry(NODE, "Floor", WindowMinimize) { DomNodeBuilder { Floor() } },
            Entry(NODE, "Wall", Stop) { DomNodeBuilder { Wall() } },
            Entry(NODE, "Window") { DomNodeBuilder { Window() } },
            Entry(NODE, "Column", ICursor) { DomNodeBuilder { Column() } },
            Entry(NODE, "Staircase", Signal) { DomNodeBuilder { Staircase() } },
            Entry(NODE, "Slope") { DomNodeBuilder { Slope() } },
            Entry(NODE, "SlopedRoof", Campground) { DomNodeBuilder { SlopedRoof() } },

            Entry(BUILDING, "Turret", ChessRook) { DomPolyRoomWithTurretBuilder() },
            Entry(BUILDING, "Turret Decor", ChessRook) { DomTurretDecor() },
            Entry(BUILDING, "Turret Roof Decor", ChessRook) { DomTurretRoofDecor() },
            Entry(BUILDING, "Turret Bottom Decor", CaretDown) { DomTurretBottomDecor() },
            Entry(BUILDING, "Gable Roof Decor", Campground) { DomGableRoofDecor() },
            Entry(BUILDING, "Arched Window", Archway) { DomNodeBuilder { Window() }.addStyle(BLD_ARCHED_WINDOW) },
            Entry(BUILDING, "Arched Bridge", Archway) { DomNodeBuilder { Wall() }.addStyle(BLD_ARCHED_BRIDGE) },

            Entry(SELECT, "Extend", ArrowsAlt) { DomExtend() },
            Entry(SELECT, "All Walls", Star) { DomPolySegmentBuilder() },
            Entry(SELECT, "Four Walls", Square) { DomFourWallsBuilder() },
            Entry(SELECT, "Select Walls") { DomSelectWalls() },
            Entry(SELECT, "Random Wall") { DomRandomSegmentBuilder() },
            Entry(SELECT, "All Corners", "..") { DomLogicPolyCornerBuilder() },
            Entry(SELECT, "Four Corners", "::") { DomLogicFourCornerBuilder() },
            Entry(SELECT, "Subdivide", BorderNone) { DomSubdivide() },
            Entry(SELECT, "Repeat", EllipsisH) { DomRepeat() },

            Entry(LOGIC, "Blueprint", Code) { DomRunBlueprint() },
            Entry(LOGIC, "Out slot", CaretRight) { DomBlueprintOutSlot() },
            Entry(LOGIC, "Random") { DomRandom() },
        )
    }

    val domBuildersByName: Map<String, Entry> by lazy {
        allDomBuilders.associateBy { it.name }
    }
    val domBuildersByGroup: Map<Group, List<Entry>> by lazy {
        allDomBuilders.groupBy { it.group }
    }

    fun create(name: String): DomBuilder? = domBuildersByName[name]?.create?.invoke()

    enum class Group(val label: String) {
        NODE("Basic nodes"),
        BUILDING("Building components"),
        SELECT("Selectors"),
        LOGIC("Logic"),
    }

    data class Entry(
        val group: Group,
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
                group: Group,
                name: String,
                icon: String = "",
                noinline create: () -> T,
            ) = Entry(group, name, icon, create, T::class.java)
        }
    }
}