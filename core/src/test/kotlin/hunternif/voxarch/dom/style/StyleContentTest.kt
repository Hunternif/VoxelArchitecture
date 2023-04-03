package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.property.content
import hunternif.voxarch.dom.style.property.size
import hunternif.voxarch.plan.Room
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

    @Test
    fun `prevent infinite recursion`() {
        // expected
        val refDom = domRoot {
            room("extend") {
                node("new_node", "extend") {
                    node("new_node", "extend")
                }
            }
            room("second", "extend") {
                node("new_node", "extend") {
                    node("new_node", "extend")
                }
            }
        }.buildDom()

        // actual
        val style = Stylesheet().add {
            style("extend") {
                content {
                    node("new_node", "extend")
                }
            }
        }
        val dom = domRoot {
            room("extend")
            room("second", "extend")
        }.buildDom(style)

        assertNodeTreeEqualsRecursive(refDom, dom)
    }

    @Test
    fun `execute multiple times`() {
        val style = Stylesheet().add {
            style("extend") {
                content {
                    node("new_node")
                }
            }
        }
        val room = DomNodeBuilder { Room() }.apply { addStyle("extend") }

        val dom1 = domRoot { addChild(room) }.buildDom(style)
        val dom2 = domRoot { addChild(room) }.buildDom(style)

        assertNodeTreeEqualsRecursive(dom1, dom2)
    }
}