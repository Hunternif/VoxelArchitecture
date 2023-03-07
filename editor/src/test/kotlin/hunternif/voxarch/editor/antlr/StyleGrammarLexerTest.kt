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

    @Test
    fun `tokenize string literals`() {
        assertTokensEqual(
            listOf(STR, STR, ID, STR),
            tokenize("""
                "string's" '"my" string' non-string
                "string that doesn't terminate
            """.trimIndent())
        )
    }

    @Test
    fun `tokenize number literals`() {
        assertTokensEqual(
            listOf(INT, INT, FLOAT, INT_PCT, FLOAT_PCT),
            tokenize("0  5  130.1  28%  17.5%")
        )
    }

    @Test
    fun `tokenize expressions`() {
        assertTokensEqual(
            listOf(LPAREN, INT, DIV, FLOAT, RPAREN, TILDE, INT_PCT, MULT, INT),
            tokenize("(5 / 2.0) ~ 18% * 3")
        )
    }

    @Test
    fun `tokenize complex selector`() {
        assertTokensEqual(
            listOf(DOT, ID, GT, LBRACKET, DOT, ID, DOT, ID, RBRACKET, ID, DOT, ID, DOT, ID, COMMA, ID),
            tokenize(".castle > [.base.test] Room.abc.size-2, Wall")
        )
    }

    @Test
    fun `tokenize comments`() {
        assertTokensEqual(
            listOf(COMMENT, ID, BLOCK_COMMENT, INT, COMMENT),
            tokenize("""
                // hello world
                abc /* multi-
                line comment */
                123 // end-of line comment
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
