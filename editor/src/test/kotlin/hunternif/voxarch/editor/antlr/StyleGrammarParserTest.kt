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

    @Test(expected = ParseCancellationException::class)
    fun `parse invalid unmatched brace`() {
        parseAndFormat("selector { prop: value")
    }

    @Test(expected = ParseCancellationException::class)
    fun `parse invalid prop without value`() {
        parseAndFormat("""
            selector {
                prop1:
                prop2: value
            }
        """.trimIndent())
    }

    @Test
    fun `parse base selector`() {
        assertEquals("""
            Stylesheet
              StyleRule
                TypeSelector
                  T[selector]
                T[{]
                RuleBody
                  Declaration
                    T[property]
                    T[:]
                    EnumValue
                      T[value]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                selector {
                    property: value
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse inline selector`() {
        assertEquals("""
            Stylesheet
              StyleRule
                TypeSelector
                  T[selector]
                T[{]
                RuleBody
                  Declaration
                    T[prop1]
                    T[:]
                    EnumValue
                      T[value1]
                    T[;]
                  Declaration
                    T[prop2]
                    T[:]
                    EnumValue
                      T[value2]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("selector { prop1: value1; prop2: value2 }")
        )
    }

    @Test
    fun `parse class selector`() {
        assertEquals("""
            Stylesheet
              StyleRule
                ClassSelector
                  DotClass
                    T[.]
                    T[tower-4]
                T[{]
                RuleBody
                  Declaration
                    T[diameter]
                    T[:]
                    NumValue
                      IntLiteral
                        T[4]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                .tower-4 {
                    diameter: 4
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse type + class selector`() {
        assertEquals("""
            Stylesheet
              StyleRule
                AndSelector
                  TypeSelector
                    T[Room]
                  ClassSelector
                    DotClass
                      T[.]
                      T[tower]
                T[{]
                RuleBody
                  Declaration
                    T[height]
                    T[:]
                    InheritValue
                      T[inherit]
                  Declaration
                    T[width]
                    T[:]
                    NumValue
                      NumBinaryOperation
                        IntLiteral
                          T[4]
                        T[~]
                        IntPctLiteral
                          T[50%]
                    T[// randomized value, 4 is natural size in voxels]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                Room.tower {
                    height: inherit
                    width: 4 ~ 50%    // randomized value, 4 is natural size in voxels
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse multiple AND-combined class selectors`() {
        assertEquals("""
            Stylesheet
              StyleRule
                AndSelector
                  AndSelector
                    ClassSelector
                      DotClass
                        T[.]
                        T[tower]
                    ClassSelector
                      DotClass
                        T[.]
                        T[tall]
                  ClassSelector
                    DotClass
                      T[.]
                      T[a]
                T[{]
                RuleBody
                  Declaration
                    T[height]
                    T[:]
                    NumValue
                      IntPctLiteral
                        T[150%]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                .tower.tall.a {
                    height: 150%
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse multiple OR-combined selectors`() {
        assertEquals("""
            Stylesheet
              StyleRule
                OrSelector
                  OrSelector
                    ClassSelector
                      DotClass
                        T[.]
                        T[tower]
                    T[,]
                    AndSelector
                      ClassSelector
                        DotClass
                          T[.]
                          T[tall]
                      ClassSelector
                        DotClass
                          T[.]
                          T[room]
                  T[,]
                  TypeSelector
                    T[Prop]
                T[{]
                RuleBody
                  Declaration
                    T[rotation]
                    T[:]
                    NumValue
                      FloatLiteral
                        T[0.0]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                .tower, .tall.room, Prop {
                    rotation: 0.0
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse descendant selector`() {
        assertEquals("""
            Stylesheet
              StyleRule
                DescendantSelector
                  T[[]
                  ClassSelector
                    DotClass
                      T[.]
                      T[spire-castle]
                  T[]]
                  ClassSelector
                    DotClass
                      T[.]
                      T[tower]
                T[{]
                RuleBody
                  Declaration
                    T[roof-shape]
                    T[:]
                    EnumValue
                      T[SPIRE]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                [.spire-castle] .tower {
                    roof-shape: SPIRE
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse multiple OR-combined descendant selectors`() {
        assertEquals("""
            Stylesheet
              StyleRule
                DescendantSelector
                  T[[]
                  OrSelector
                    ClassSelector
                      DotClass
                        T[.]
                        T[inner-wall]
                    T[,]
                    ClassSelector
                      DotClass
                        T[.]
                        T[outer-wall]
                  T[]]
                  TypeSelector
                    T[wall]
                T[{]
                RuleBody
                  Declaration
                    T[depth]
                    T[:]
                    NumValue
                      NumMinusExpression
                        T[-]
                        IntLiteral
                          T[1]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                [.inner-wall, .outer-wall] wall {
                    depth: -1
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse direct child selector`() {
        assertEquals("""
            Stylesheet
              StyleRule
                ChildSelector
                  ClassSelector
                    DotClass
                      T[.]
                      T[tower-wall]
                  T[>]
                  ClassSelector
                    DotClass
                      T[.]
                      T[window]
                T[{]
                RuleBody
                  Declaration
                    T[padding-x]
                    T[:]
                    NumValue
                      IntLiteral
                        T[1]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                .tower-wall > .window {
                    padding-x: 1
                }
            """.trimIndent())
        )
    }

    @Test
    fun `parse blueprint execution rule`() {
        assertEquals("""
            Stylesheet
              StyleRule
                ClassSelector
                  DotClass
                    T[.]
                    T[turret]
                T[{]
                RuleBody
                  Declaration
                    T[blueprint]
                    T[:]
                    StrValue
                      T["Turret Decor BP"]
                T[}]
              T[<EOF>]
        """.trimIndent(),
            parseAndFormat("""
                .turret {
                    blueprint: "Turret Decor BP"
                }
            """.trimIndent())
        )
    }

    private fun parseAndFormat(input: String): String {
        val lexer = StyleGrammarLexer(CharStreams.fromString(input))
        val parser = StyleGrammarParser(CommonTokenStream(lexer))
        parser.errorHandler = BailErrorStrategy()
//        parser.addErrorListener(ConsoleErrorListener())
        return parser.stylesheet().toParseTree().multiLineString()
    }
}
