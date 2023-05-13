package hunternif.voxarch.dom

import hunternif.voxarch.util.assertNodeTreeEqualsRecursive
import org.junit.Test

class DomRandomTest {
    @Test
    fun `execute random child element`() {
        val dom = domRoot {
            random {
                node("first")
                node("second")
            }
        }
        val ref1 = domRoot { node("first") }.buildDom()
        val ref2 = domRoot { node("second") }.buildDom()

        val tree1 = dom.buildDom(seed = 1)
        val tree2 = dom.buildDom(seed = 4)

        assertNodeTreeEqualsRecursive(ref1, tree1)
        assertNodeTreeEqualsRecursive(ref2, tree2)
    }
}