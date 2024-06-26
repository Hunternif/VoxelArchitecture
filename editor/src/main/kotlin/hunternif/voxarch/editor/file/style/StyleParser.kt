package hunternif.voxarch.editor.file.style

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.antlr.StyleGrammarLexer
import hunternif.voxarch.editor.antlr.StyleGrammarParser
import hunternif.voxarch.editor.antlr.StyleGrammarParser.*
import hunternif.voxarch.editor.blueprint.DomBuilderFactory
import hunternif.voxarch.editor.blueprint.styleEditorStyleProperties
import hunternif.voxarch.vector.Vec3
import org.antlr.v4.runtime.*

class StyleParser {
    private val errorListener = StyleErrorListener()

    /** Parses a list of styles from text. See "CSS" specification. */
    fun parseStylesheet(
        styleStr: String,
    ): StyleParseResult {
        errorListener.errors.clear()
        if (styleStr.isEmpty()) return StyleParseResult(emptyList(), emptyList())
        val lexer = StyleGrammarLexer(CharStreams.fromString(styleStr))
        val parser = StyleGrammarParser(CommonTokenStream(lexer))

        parser.errorListeners.clear()
        parser.addErrorListener(errorListener)
        val rules = parser.stylesheet().toRules()
        return StyleParseResult(rules, errorListener.errors)
    }

    /**
     * Parses a list of declarations, i.e. a rule without a selector.
     * Returns a list with a single rule with no selectors.
     */
    fun parseDeclarations(
        styleStr: String,
    ): StyleParseResult {
        errorListener.errors.clear()
        if (styleStr.isEmpty()) return StyleParseResult(emptyList(), emptyList())
        val lexer = StyleGrammarLexer(CharStreams.fromString(styleStr))
        val parser = StyleGrammarParser(CommonTokenStream(lexer))

        parser.errorListeners.clear()
        parser.addErrorListener(errorListener)
        val rule = Rule()
        parser.ruleBody()?.declaration()?.forEach {
            rule.add(it.toDecl())
        }
        return StyleParseResult(listOf(rule), errorListener.errors)
    }

    private fun StylesheetContext.toRules() = styleRule().map { it.toRule() }
    private fun StyleRuleContext.toRule(): Rule {
        val rule = Rule(selector().toSelectors())
        val declarations = ruleBody()?.declaration()?.map { it.toDecl() } ?: emptyList()
        rule.declarations.addAll(declarations)
        return rule
    }

    private fun SelectorContext?.toSelectors(): List<Selector> = when (this) {
        null -> emptyList()
        is OrSelectorContext -> left.toSelectors() + right.toSelectors()
        is AnySelectorContext -> emptyList()  // '*' doesn't change any conditions
        else -> listOf(Selector().append(this))
    }

    private fun Selector.append(other: SelectorContext?): Selector = apply {
        when (other) {
            null -> {}
            is OrSelectorContext -> {}   // should never happen
            is AnySelectorContext -> {}  // '*' doesn't change any conditions
            is AndSelectorContext -> {
                append(other.left)
                append(other.right)
            }
            is ChildSelectorContext -> {
                parentSelectors.addAll(other.parent.toSelectors())
                append(other.child)
            }
            is DescendantSelectorContext -> {
                ancestorSelectors.addAll(other.ancestor.toSelectors())
                append(other.descendant)
            }
            is TypeSelectorContext -> {
                val typename = other.ID()?.text
                val type = knownStyleTypes[typename]
                if (type == null) {
                    isInvalid = true
                    val token = other.ID()?.symbol
                    errorListener.addError(
                        typename,
                        token?.line ?: 0,
                        token?.charPositionInLine ?: 0,
                        "invalid selector"
                    )
                } else {
                    types.add(type)
                }
            }
            is ClassSelectorContext -> {
                val styleClass = other.ID()?.text
                if (styleClass != null) styleClasses.add(styleClass)
            }
        }
    }

    private fun DeclarationContext.toDecl(): Declaration<*> {
        val property = knownStyleProperties[property.text]
            ?: invalidProperty(property.text)
        return makeDecl(property, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> makeDecl(
        property: Property<T>, ctx: PropValueContext?,
    ): Declaration<T> {
        val value: Value<T> = when {
            ctx is InheritValueContext -> inherit()
            ctx is EnumValueContext && property.isEnum -> {
                // TODO: add support for random enum expressions
                val name = ctx.ID()?.text ?: ""
                val enum = property.valType.enumConstants?.firstOrNull { (it as Enum<*>).name == name }
                enum?.let { set(name, it) }
            }
            ctx is StrValueContext && property.isString -> {
                set((ctx.STR()?.text?.stripQuotes() ?: "") as T)
            }
            ctx is NumValueContext && property.isNumber -> {
                try {
                    ctx.numExpression()?.toValue() as? Value<T>
                } catch (e: Exception) {
                    null // invalid value
                }
            }
            ctx is IntVec3ValueContext && property.isType<Vec3>() -> {
                val (x, y, z) = ctx.INT().map { it.text.toInt() }
                set("$x $y $z", Vec3(x, y, z) as T)
            }
            ctx is Vec3ValueContext && property.isType<Vec3>() -> {
                val (x, y, z) = ctx.number().map { it.text.toDouble() }
                set("$x $y $z", Vec3(x, y, z) as T)
            }
            else -> null
        }
        // null means invalid value, so fall back to default:
            ?: set("invalid", property.default)
        return Declaration(property, value)
    }

    @Throws(NumberExpressionException::class)
    private fun NumExpressionContext?.toValue(): Value<Number> = when (this) {
        is IntLiteralContext -> INT().text.toInt().vx
        is IntPctLiteralContext -> INT_PCT().text.removeSuffix("%").toInt().pct
        is FloatLiteralContext -> FLOAT().text.toDouble().vx
        is FloatPctLiteralContext -> FLOAT_PCT().text.removeSuffix("%").toDouble().pct
        is NumMinusExpressionContext -> -numExpression().toValue()
        is NumParenExpressionContext -> numExpression().toValue()
        is NumBinaryOperationContext -> when (op.text) {
            "+" -> left.toValue() + right.toValue()
            "-" -> left.toValue() - right.toValue()
            "*" -> left.toValue() * right.toValue()
            "/" -> left.toValue() / right.toValue()
            "~" -> left.toValue() to right.toValue()
            else -> throw NumberExpressionException(this.text)
        }
        else -> throw NumberExpressionException(this?.text ?: "")
    }


    /** All known Node and DomBuilder subclasses that can be styled.
     * Maps class name to class. */
    val knownStyleTypes: Map<String, Class<*>> by lazy {
        DomBuilderFactory.allDomBuilders.associate {
            val clazz = it.nodeClass ?: it.clazz
            clazz.simpleName to clazz
        }
    }

    val knownStyleProperties: Map<String, Property<*>> by lazy {
        styleEditorStyleProperties.associateBy { it.name }
    }

    private fun invalidProperty(text: String) = object : Property<Any>(
        text, Any::class.java, Any::class.java, Any()
    ) {
        override fun applyTo(styled: StyledElement<*>, value: Value<Any>) {}
    }

    private fun String.stripQuotes() = this.trim('\'', '\"')

    class NumberExpressionException(
        text: String,
        cause: Exception? = null,
    ) : Exception("failed to parse number expression: $text", cause)

    data class StyleParseResult(
        val rules: List<Rule>,
        val errors: List<StyleParseError>,
    )

    class StyleErrorListener() : BaseErrorListener() {
        val errors: MutableList<StyleParseError> = mutableListOf()
        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?,
        ) {
            addError(offendingSymbol, line, charPositionInLine, msg, e)
        }

        fun addError(
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String? = "error",
            e: RecognitionException? = null,
        ) {
            errors.add(StyleParseError(offendingSymbol, line, charPositionInLine, msg, e))
        }
    }
}

data class StyleParseError(
    val offendingSymbol: Any?,
    val line: Int,
    val charPositionInLine: Int,
    val parserMsg: String?,
    val e: RecognitionException?,
) {
    val msg = "line $line:$charPositionInLine $parserMsg"
    override fun toString(): String = msg
}
