package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.editor.util.assertDomTreeStructureEqualsRecursive
import org.junit.Assert.*
import org.junit.Test

class DomBuilderFactoryTest {
    @Test
    fun `ensure all DomBuilders can be created`() {
        DomBuilderFactory.allDomBuilders.forEach { (_, factory) ->
            val domBuilder = factory()
            assertNotNull(domBuilder)
        }
    }

    @Test
    fun `ensure all DomBuilders don't modify DOM during build`() {
        DomBuilderFactory.allDomBuilders.forEach { (_, factory) ->
            val originalDom = domRoot { addChild(factory()) }
            val builtDom = domRoot { addChild(factory()) }
            builtDom.buildDom()
            assertDomTreeStructureEqualsRecursive(originalDom, builtDom)
        }
    }

    @Test
    fun `all DomBuilders have unique names`() {
        val nameSet = DomBuilderFactory.domBuildersByName.keys
        assertEquals(nameSet.size, DomBuilderFactory.allDomBuilders.size)
    }
}