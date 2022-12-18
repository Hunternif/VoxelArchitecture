package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.editor.util.assertDomTreeStructureEqualsRecursive
import org.junit.Assert.*
import org.junit.Test

class DomBuilderFactoryTest {
    @Test
    fun `ensure all DomBuilders can be created`() {
        domBuilderFactoryByName.forEach { (_, factory) ->
            val domBuilder = factory()
            assertNotNull(domBuilder)
        }
    }

    @Test
    fun `ensure all DomBuilders don't modify DOM during build`() {
        domBuilderFactoryByName.forEach { (_, factory) ->
            val originalDom = domRoot { addChild(factory()) }
            val builtDom = domRoot { addChild(factory()) }
            builtDom.buildDom()
            assertDomTreeStructureEqualsRecursive(originalDom, builtDom)
        }
    }
}