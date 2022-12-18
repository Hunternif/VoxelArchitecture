package hunternif.voxarch.snapshot

import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.dom.style.property.position
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.dom.style.property.width
import hunternif.voxarch.dom.style.vx
import org.junit.Test

class DomExtendSnapshotTest : BaseSnapshotTest(12, 10, 12) {
    @Test
    fun `extend room NSWE`() {
        val style = defaultStyle.add {
            style("base") {
                position(5.vx, 0.vx, 5.vx)
                size(4.vx, 2.vx, 3.vx)
            }
            style("child") {
                width { 2.vx }
            }
        }
        val dom = domRoot {
            roomWithWalls("base") {
                extend {
                    north.run {
                        roomWithWalls("child")
                    }
                    south.run {
                        roomWithWalls("child")
                    }
                    east.run {
                        roomWithWalls("child")
                    }
                    west.run {
                        roomWithWalls("child")
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