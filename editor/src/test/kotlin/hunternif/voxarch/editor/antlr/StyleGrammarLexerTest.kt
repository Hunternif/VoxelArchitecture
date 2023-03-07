package hunternif.voxarch.editor.antlr

import hunternif.voxarch.editor.antlr.StyleGrammarLexer.*
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test

class StyleGrammarLexerTest {
    @Test
    fun `tokenize base selector`() {
        assertTokensEqual(
            listOf(ID, LBRACE, ID, COLON, ID, SEMICOLON, RBRACE),
            tokenize("selector { property: value; }")
        )
    }

    @Test
    fun `tokenize multiline selector`() {
        assertTokensEqual(
            listOf(ID, LBRACE, ID, COLON, ID, ID, COLON, ID, RBRACE ),
            tokenize("""
                selector {
                    prop1: value1
                    prop2: value2
                    
                }
            """.trimIndent())
        )
    }

    /** Returns token types */
    private fun tokenize(input: String): List<Int> {
        val lexer = StyleGrammarLexer(CharStreams.fromString(input))
        return lexer.allTokens.map { it.type }
    }

    /** Matches lists of tokens based on their human-readable name */
    private fun assertTokensEqual(expected: List<Int>, actual: List<Int>) {
        assertEquals(
            expected.map { VOCABULARY.getSymbolicName(it) },
            actual.map { VOCABULARY.getSymbolicName(it) }
        )
    }
}
