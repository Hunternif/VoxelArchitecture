package hunternif.voxarch.dom

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.*
import org.junit.Assert.assertTrue
import org.junit.Test

class VisibilityTest {
    @Test
    fun `both visible`() {
        val style = Stylesheet().add {
            style("parent") { visible() }
            style("child") { visible() }
        }
        val dom = domRoot(style) {
            node("parent") {
                node("child")
            }
        }.buildDom()

        val parent = dom.children.firstOrNull()
        val child = parent?.children?.firstOrNull()

        assertTrue(parent != null)
        assertTrue(child != null)
    }

    @Test
    fun `child gone`() {
        val style = Stylesheet().add {
            style("parent") { visible() }
            style("child") { gone() }
        }
        val dom = domRoot(style) {
            node("parent") {
                node("child")
            }
        }.buildDom()

        val parent = dom.children.firstOrNull()
        val child = parent?.children?.firstOrNull()

        assertTrue(parent != null)
        assertTrue(child == null)
    }

    @Test
    fun `parent gone`() {
        val style = Stylesheet().add {
            style("parent") { gone() }
            style("child") { visible() }
        }
        val dom = domRoot(style) {
            node("parent") {
                node("child")
            }
        }.buildDom()

        val parent = dom.children.firstOrNull()
        val child = parent?.children?.firstOrNull()

        assertTrue(parent == null)
        assertTrue(child == null)
    }
}