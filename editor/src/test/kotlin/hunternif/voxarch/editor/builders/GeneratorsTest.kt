package hunternif.voxarch.editor.builders

import hunternif.voxarch.editor.AppStateImpl
import hunternif.voxarch.editor.builder.createGeneratorByName
import hunternif.voxarch.generator.IGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.reflections.Reflections

class GeneratorsTest {
    @Test
    fun `ensure all generators can be created`() {
        val state = AppStateImpl()
        val reflections = Reflections("hunternif.voxarch")
        val classes = reflections.getSubTypesOf(IGenerator::class.java)
        val classNames = classes.map { it.simpleName }
        assertEquals(classNames.toSet(), state.generatorNames.toSet())
        for (name in state.generatorNames) {
            val generator = state.createGeneratorByName(name)
            assertNotNull(generator)
        }
    }
}