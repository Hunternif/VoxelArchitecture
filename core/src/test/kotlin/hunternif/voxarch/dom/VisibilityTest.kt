package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.*
import org.junit.Assert.assertTrue
import org.junit.Test

class VisibilityTest {
    @Test
    fun `both visible`() {
        val style = Stylesheet().apply {
            style2("parent") { visible2() }
            style2("child") { visible2() }
        }
        val dom = DomRoot(style).apply {
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
        val style = Stylesheet().apply {
            style2("parent") { visible2() }
            style2("child") { gone2() }
        }
        val dom = DomRoot(style).apply {
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
        val style = Stylesheet().apply {
            style2("parent") { gone2() }
            style2("child") { visible2() }
        }
        val dom = DomRoot(style).apply {
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