package hunternif.voxarch.editor.antlr

import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.ParseCancellationException
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

    @Test(expected = ParseCancellationException::class)
    fun `parse invalid empty selector`() {
        parseAndFormat("{ prop: value }")
    }

    private fun parseAndFormat(input: String): String {
        val lexer = StyleGrammarLexer(CharStreams.fromString(input))
        val parser = StyleGrammarParser(CommonTokenStream(lexer))
        parser.errorHandler = BailErrorStrategy()
        return parser.stylesheet().toParseTree().multiLineString()
    }
}
