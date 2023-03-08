package hunternif.voxarch.editor.file.style

import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.Selector
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
    return Rule(selector().toSelectors())
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