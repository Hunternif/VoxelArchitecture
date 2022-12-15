package hunternif.voxarch.editor.blueprint

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
}