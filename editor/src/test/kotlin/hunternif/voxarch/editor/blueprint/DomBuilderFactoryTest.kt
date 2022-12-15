package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.domRoot
import org.junit.Assert.*
import org.junit.Test

class DomBuilderFactoryTest {
    @Test
    fun `ensure all DomBuilders can be created`() {
        val root = domRoot()
        domBuilderFactoryByName.forEach { (_, factory) ->
            val domBuilder = factory(root)
            assertNotNull(domBuilder)
        }
    }
}