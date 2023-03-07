package hunternif.voxarch.editor.antlr

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Assert.assertEquals
import org.junit.Test

class StyleGrammarParserTest {
    @Test
    fun `parse empty stylesheet`() {
        assertEquals("""
            Stylesheet
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("")
        )
    }

    private fun parseAndFormat(input: String): String {
        val lexer = StyleGrammarLexer(CharStreams.fromString(input))
        val parser = StyleGrammarParser(CommonTokenStream(lexer))
        return parser.stylesheet().toParseTree().multiLineString()
    }
}
