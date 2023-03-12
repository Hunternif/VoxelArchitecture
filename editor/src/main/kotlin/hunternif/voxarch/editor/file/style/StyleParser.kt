package hunternif.voxarch.editor.file.style

import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.antlr.StyleGrammarLexer
import hunternif.voxarch.editor.antlr.StyleGrammarParser
import hunternif.voxarch.editor.antlr.StyleGrammarParser.*
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import org.antlr.v4.runtime.*

/** Parses a list of styles from text. See "CSS" specification. */
@Throws(StyleParseException::class)
fun parseStylesheet(styleStr: String): List<Rule> {
    if (styleStr.isEmpty()) return emptyList()
    val lexer = StyleGrammarLexer(CharStreams.fromString(styleStr))
    val parser = StyleGrammarParser(CommonTokenStream(lexer))
    parser.addErrorListener(StyleErrorListener())
    return parser.stylesheet().toRules()
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
            knownStyleTypes[typename]?.let { types.add(it) }
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
            val enum = property.valType.enumConstants?.first { (it as Enum<*>).name == name }
            enum?.let { set(name, it) }
        }
        ctx is StrValueContext && property.isType<String>() -> {
            // TODO: strip quotes
            set((ctx.STR()?.text ?: "") as T)
        }
        ctx is NumValueContext && property.isType<Number>() -> {
            try {
                ctx.numExpression()?.toValue() as Value<T>
            } catch (e: Exception) {
                null
            }
        }
        else -> null
    }
    // invalid value, use default:
        ?: set(ctx?.text ?: "null", property.default)
    return Declaration(property, value)
}

@Throws(NumberExpressionException::class)
private fun NumExpressionContext?.toValue(): Value<Number> {
    try {
        return when (this) {
            is IntLiteralContext -> INT().text.toInt().vx
            is IntPctLiteralContext -> INT_PCT().text.toInt().pct
            is FloatLiteralContext -> FLOAT().text.toDouble().vx
            is FloatPctLiteralContext -> FLOAT_PCT().text.toDouble().pct
            else -> throw NumberExpressionException()
        }
    } catch (e: Exception) {
        throw NumberExpressionException(cause = e)
    }
}


/** All known Node and DomBuilder subclasses that can be styled.
 * Maps class name to class. */
val knownStyleTypes: Map<String, Class<*>> by lazy {
    domBuilderFactoryByName.values.associate {
        val clazz = when (val domBuilder = it()) {
            is DomNodeBuilder<*> -> domBuilder.nodeClass
            else -> domBuilder::class.java
        }
        clazz.simpleName to clazz
    }
}

val knownStyleProperties: Map<String, Property<*>> by lazy {
    GlobalStyleOrder.associateBy { it.name }
}

private fun invalidProperty(text: String) = object : Property<Any>(
    text, Any::class.java, Any::class.java, Any()
) {
    override fun applyTo(styled: StyledElement<*>, value: Value<Any>) {}
}

class NumberExpressionException(
    text: String = "failed to parse number expression",
    cause: Exception? = null,
) : Exception(text, cause)

@Suppress("unused", "MemberVisibilityCanBePrivate")
class StyleParseException(
    val offendingSymbol: Any?,
    val line: Int,
    val charPositionInLine: Int,
    val parserMsg: String?,
    val e: RecognitionException?,
) : Exception("line $line:$charPositionInLine $parserMsg", e)

private class StyleErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?,
    ) {
        throw StyleParseException(offendingSymbol, line, charPositionInLine, msg, e)
    }
}