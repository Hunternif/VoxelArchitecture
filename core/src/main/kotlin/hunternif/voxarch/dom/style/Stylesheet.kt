package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.dom.style.PropertyMap.Companion.toPropertyMap

@DslMarker
annotation class StyleDsl

/** Used as the base for Style DSL. */
@StyleDsl
interface StyleParameter

/** Contains for styling DOM elements. */
@StyleDsl
open class Stylesheet {
    private val allRules = mutableListOf<Rule>()
    private val rulesByClass: ListMultimap<String, Rule> = ArrayListMultimap.create()

    /** Starting point to new style Rules. */
    fun add(block: RuleBuilder.() -> Unit): Stylesheet {
        RuleBuilder(this).apply(block)
        return this
    }

    fun addRule(rule: Rule) {
        allRules.add(rule)
        if (rule.selectors.isEmpty()) {
            rulesByClass.put(GLOBAL_STYLE, rule)
        }
        rule.selectors.forEach { sel ->
            if (sel.styleClasses.isEmpty()) {
                rulesByClass.put(GLOBAL_STYLE, rule)
            } else {
                sel.styleClasses.forEach { rulesByClass.put(it, rule) }
            }
        }
    }

    fun getProperties(element: StyledElement<*>): PropertyMap {
        val styleClasses = listOf(GLOBAL_STYLE) + element.styleClass
        val rules = styleClasses
            .flatMap { rulesByClass[it] }
            .toSet() // filter duplicates
            .filter { it.appliesTo(element) }
        return rules.flatMap { it.declarations }
            .sortedBy { GlobalStyleOrderIndex[it.property] }
            .toPropertyMap()
    }

    open fun applyStyle(element: StyledElement<*>) {
        getProperties(element).forEach { it.applyTo(element) }
    }

    fun clear() {
        rulesByClass.clear()
    }

    /** Create a copy of this stylesheet with the same rules. */
    fun copy(): Stylesheet = Stylesheet().also { it.copyRules(this) }

    /** Copy all rules from the [other] stylesheet into this stylesheet. */
    fun copyRules(other: Stylesheet) {
        allRules.addAll(other.allRules)
        other.rulesByClass.forEach { name, rule -> rulesByClass.put(name, rule) }
    }

    override fun toString(): String = allRules.joinToString("\n\n")

    companion object {
        const val GLOBAL_STYLE = "__global_style__"
    }
}
