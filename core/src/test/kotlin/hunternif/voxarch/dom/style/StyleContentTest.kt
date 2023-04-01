package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.property.content
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.util.assertNodeTreeEqualsRecursive
import org.junit.Test

class StyleContentTest {
    @Test
    fun `add content and respect other styles`() {
        // expected
        val refStyle = Stylesheet().add {
            style("parent") {
                size(2.vx, 4.vx, 6.vx)
            }
            style("child") {
                size(50.pct, 50.pct, 50.pct)
            }
        }
        val refDom = domRoot {
            room("parent", "extend") {
                node("child")
            }
        }.buildDom(refStyle)

        // actual
        val style = Stylesheet().add {
            style("parent") {
                size(2.vx, 4.vx, 6.vx)
            }
            style("child") {
                size(50.pct, 50.pct, 50.pct)
            }
            style("extend") {
                content {
                    node("child")
                }
            }
        }
        val dom = domRoot {
            room("parent", "extend")
        }.buildDom(style)

        assertNodeTreeEqualsRecursive(refDom, dom)
    }
}