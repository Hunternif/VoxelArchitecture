package hunternif.voxarch.editor.antlr

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.*

// Courtesy of https://github.com/ftomassetti/LangSandbox/blob/master/src/test/kotlin/me/tomassetti/sandy/ParseTree.kt
// TODO: include license notice

abstract class ParseTreeElement {
    abstract fun multiLineString(indent: String = ""): String
}

class ParseTreeLeaf(val text: String) : ParseTreeElement() {
    override fun toString(): String {
        return "T[$text]"
    }

    override fun multiLineString(indent: String): String = "${indent}T[$text]"
}

class ParseTreeNode(val name: String) : ParseTreeElement() {
    val children = LinkedList<ParseTreeElement>()
    fun child(c: ParseTreeElement): ParseTreeNode {
        children.add(c)
        return this
    }

    override fun toString(): String {
        return "Node($name) $children"
    }

    override fun multiLineString(indent: String): String {
        val sb = StringBuilder()
        sb.append("${indent}$name")
        children.forEach { c -> sb.append('\n').append(c.multiLineString("$indent  ")) }
        return sb.toString()
    }
}

fun ParserRuleContext.toParseTree(): ParseTreeNode {
    val res = ParseTreeNode(this::class.java.simpleName.removeSuffix("Context"))
    children?.forEach { c ->
        when (c) {
            is ParserRuleContext -> res.child(c.toParseTree())
            is TerminalNode -> res.child(ParseTreeLeaf(c.text))
        }
    }
    return res
}
