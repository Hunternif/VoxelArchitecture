package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap

@DslMarker
annotation class StyleDsl

/** Used as the base for Style DSL. */
@StyleDsl
interface StyleParameter

/** Contains for styling DOM elements. */
@StyleDsl
open class Stylesheet {
    val rules: ListMultimap<String, Rule> = ArrayListMultimap.create()

    /** Starting point to new style Rules. */
    fun add(block: RuleBuilder.() -> Unit): Stylesheet {
        RuleBuilder(this).apply(block)
        return this
    }

    fun addRule(rule: Rule) {
        if (rule.selector.styleClasses.isEmpty()) {
            rules.put(GLOBAL_STYLE, rule)
        } else {
            rule.selector.styleClasses.forEach { rules.put(it, rule) }
        }
    }

    fun getRulesFor(element: StyledElement<*>): List<Rule> {
        val styleClasses = listOf(GLOBAL_STYLE) + element.styleClass
        return styleClasses
            .flatMap { rules[it] }
            .toSet() // filter duplicates
            .filter { it.appliesTo(element) }
    }

    open fun applyStyle(element: StyledElement<*>) {
        getRulesFor(element)
            .flatMap { it.declarations }
            .sortedBy { GlobalStyleOrderIndex[it.property] }
            // keep only the latest declaration per property:
            .associateBy { it.property }
            .values.forEach { it.applyTo(element) }
    }

    fun clear() {
        rules.clear()
    }

    /** Create a copy of this stylesheet with the same rules. */
    fun copy(): Stylesheet = Stylesheet().also { it.copyRules(this) }

    /** Copy all rules from the [other] stylesheet into this stylesheet. */
    fun copyRules(other: Stylesheet) {
        other.rules.forEach { name, rule -> rules.put(name, rule) }
    }

    companion object {
        const val GLOBAL_STYLE = "__global_style__"
    }
}
