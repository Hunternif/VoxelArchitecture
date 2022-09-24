package hunternif.voxarch.editor.blueprint

import org.junit.Assert
import org.junit.Test

class StyleRulesTest {
    @Test
    fun `find all rules`() {
        val rules = allRules
        Assert.assertTrue(rules.isNotEmpty())
    }
}