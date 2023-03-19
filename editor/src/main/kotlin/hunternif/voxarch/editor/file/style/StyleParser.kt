package hunternif.voxarch.editor.file.style

import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.antlr.StyleGrammarLexer
import hunternif.voxarch.editor.antlr.StyleGrammarParser
import hunternif.voxarch.editor.antlr.StyleGrammarParser.*
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import hunternif.voxarch.vector.Vec3
import org.antlr.v4.runtime.*

/** Parses a list of styles from text. See "CSS" specification. */
fun parseStylesheet(
    styleStr: String,
): StyleParseResult {
    if (styleStr.isEmpty()) return StyleParseResult(emptyList(), emptyList())
    val lexer = StyleGrammarLexer(CharStreams.fromString(styleStr))
    val parser = StyleGrammarParser(CommonTokenStream(lexer))
    val errorListener = StyleErrorListener()
    parser.addErrorListener(errorListener)
    val rules = parser.stylesheet().toRules()
    return StyleParseResult(rules, errorListener.errors)
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
        ctx is StrValueContext && property.isString -> {
            // TODO: strip quotes
            set((ctx.STR()?.text ?: "") as T)
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
        else ->  throw NumberExpressionException(this.text)
    }
    else -> throw NumberExpressionException(this?.text ?: "")
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
    AllStyleProperties.associateBy { it.name }
}

private fun invalidProperty(text: String) = object : Property<Any>(
    text, Any::class.java, Any::class.java, Any()
) {
    override fun applyTo(styled: StyledElement<*>, value: Value<Any>) {}
}

class NumberExpressionException(
    text: String,
    cause: Exception? = null,
) : Exception("failed to parse number expression: $text", cause)

data class StyleParseResult(
    val rules: List<Rule>,
    val errors: List<StyleParseError>,
)

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
        errors.add(StyleParseError(offendingSymbol, line, charPositionInLine, msg, e))
    }
}