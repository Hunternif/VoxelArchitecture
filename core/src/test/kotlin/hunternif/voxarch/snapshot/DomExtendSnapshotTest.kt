package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.plan.Room
import org.junit.Test

class DomExtendSnapshotTest : BaseSnapshotTest(16, 3, 16) {
    @Test
    fun `extend room NSWE`() {
        testDirections(true, true, true, true)
    }

    @Test
    fun `extend room NSW`() {
        testDirections(true, true, false, true)
    }

    @Test
    fun `extend room NSE`() {
        testDirections(true, true, true, false)
    }

    @Test
    fun `extend room NW`() {
        testDirections(true, false, false, true)
    }

    @Test
    fun `extend room N`() {
        testDirections(true, false, false, false)
    }

    private fun testDirections(
        buildNorth: Boolean = false,
        buildSouth: Boolean = false,
        buildEast: Boolean = false,
        buildWest: Boolean = false,
    ) {
        val style = defaultStyle.add {
            style("base") {
                position(7.vx, 0.vx, 7.vx)
                size(5.vx, 2.vx, 4.vx)
                snapOrigin { floorCenter() }
            }
            style("child") {
                depth { 3.vx }
                offsetStartX { (-1).vx }
            }
        }
        val child = DomNodeBuilder { Room() }.apply {
            addStyle("child")
            floor()
            allWalls { wall() }
        }
        val dom = domRoot {
            roomWithWalls("base") {
                extend {
                    north.run {
                        if (buildNorth) addChild(child)
                    }
                    south.run {
                        if (buildSouth) addChild(child)
                    }
                    east.run {
                        if (buildEast) addChild(child)
                    }
                    west.run {
                        if (buildWest) addChild(child)
                    }
                }
            }
        }.buildDom(style)
        build(dom)
        record(out.sliceY(0))
    }

    private inline fun DomBuilder.roomWithWalls(
        vararg styleClass: String,
        crossinline block: DomBuilder.() -> Unit = {},
    ) {
        room(*styleClass) {
            floor()
            allWalls { wall() }
            block()
        }
    }
}