package hunternif.voxarch.snapshot

import hunternif.voxarch.builder.setCastleBuilders
import hunternif.voxarch.dom.*
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.plan.Window
import org.junit.Test

class TurretWindowsTest : BaseSnapshotTest(20, 20, 20) {
    override fun setup() {
        super.setup()
        context.builders.setCastleBuilders()
    }

    @Test
    fun `turret with windows`() {
        val style = defaultStyle.add {
            style("base") {
                position(2.vx, 0.vx, 2.vx)
                size(8.vx, 7.vx, 5.vx)
            }
            styleFor<Window> {
                paddingX { 1.vx }
                paddingY { 1.vx }
            }
            style(DOM_TURRET) {
                snapOrigin { corner() }
            }
        }
        val structure = domRoot {
            node("base") {
                turret()
                allWalls {
                    // Note: if I insert another wall here, the windows get overwritten
                    // This happens because it tries to build the walls first via DFS,
                    // and then the windows inside, and only after that the turret and
                    // its walls.
                    // wall {
                    //   archedWindow()
                    // }
                    // TODO: make windows get built at the end: either BFS, or
                    // build them globally after everything else.
                    archedWindow()
                }

            }
        }.buildDom(style)
        build(structure)
        recordVox()
    }

    @Test
    fun `turret decor with windows`() {
        val style = defaultStyle.add {
            style("base") {
                position(2.vx, 0.vx, 2.vx)
                size(8.vx, 7.vx, 5.vx)
            }
            styleFor<Window> {
                paddingX { 1.vx }
                paddingY { 1.vx }
            }
        }
        val structure = domRoot {
            node("base") {
                turretDecor()
                allWalls {
                    archedWindow()
                }

            }
        }.buildDom(style)
        build(structure)
        recordVox()
    }
}