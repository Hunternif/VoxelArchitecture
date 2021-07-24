package hunternif.voxarch.dom

import hunternif.voxarch.dom.builder.DomRoot
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.gone
import hunternif.voxarch.dom.style.visible
import org.junit.Assert.assertTrue
import org.junit.Test

class VisibilityTest {
    @Test
    fun `both visible`() {
        val style = Stylesheet().apply {
            style("parent") { visible() }
            style("child") { visible() }
        }
        val dom = DomRoot(style).apply {
            node("parent") {
                node("child")
            }
        }.build()

        val parent = dom.children.firstOrNull()
        val child = parent?.children?.firstOrNull()

        assertTrue(parent != null)
        assertTrue(child != null)
    }

    @Test
    fun `child gone`() {
        val style = Stylesheet().apply {
            style("parent") { visible() }
            style("child") { gone() }
        }
        val dom = DomRoot(style).apply {
            node("parent") {
                node("child")
            }
        }.build()

        val parent = dom.children.firstOrNull()
        val child = parent?.children?.firstOrNull()

        assertTrue(parent != null)
        assertTrue(child == null)
    }

    @Test
    fun `parent gone`() {
        val style = Stylesheet().apply {
            style("parent") { gone() }
            style("child") { visible() }
        }
        val dom = DomRoot(style).apply {
            node("parent") {
                node("child")
            }
        }.build()

        val parent = dom.children.firstOrNull()
        val child = parent?.children?.firstOrNull()

        assertTrue(parent == null)
        assertTrue(child == null)
    }
}