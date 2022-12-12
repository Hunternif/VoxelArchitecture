package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder

/**
 * Convenience builder for creating Rules.
 */
@CastleDsl
class RuleBuilder(
    val stylesheet: Stylesheet,
    val baseSelector: Selector = Selector.EMPTY,
) {
    /**
     * Register a style rule i.e. a list of style declarations.
     * They are not limited by Node type, they will apply wherever possible.
     * @param styleClass is the "CSS class".
     */
    fun style(
        vararg styleClass: String,
        block: Rule.() -> Unit,
    ) {
        val selector = baseSelector + select(*styleClass)
        val rule = Rule(selector).apply(block)
        stylesheet.addRule(rule)
    }

    /**
     * Register a style rule for class names, which will be applied
     * to all child elements.
     */
    fun styleInherit(
        vararg styleClass: String,
        block: Rule.() -> Unit,
    ) {
        val rule = Rule(selectInherit(*styleClass)).apply(block)
        stylesheet.addRule(rule)
    }

    /**
     * Register a style rule applied to specific DOM Builder instances.
     */
    fun styleFor(
        vararg instance: DomBuilder,
        block: Rule.() -> Unit,
    ) {
        val selector = baseSelector + select(*instance)
        val rule = Rule(selector).apply(block)
        stylesheet.addRule(rule)
    }

    /**
     * Register a style rule i.e. a list of style declarations.
     * These will be limited by type [T].
     * @param styleClass is the "CSS class".
     */
    inline fun <reified T> styleFor(
        vararg styleClass: String,
        noinline block: Rule.() -> Unit,
    ) {
        val selector = baseSelector + select(T::class.java).style(*styleClass)
        val rule = Rule(selector).apply(block)
        stylesheet.addRule(rule)
    }

    /**
     * Allows to create multiple style rules, each including the same base
     * classes in the selector.
     */
    fun styleFamily(
        styleClass: String,
        inheritStyleClass: String? = null,
        block: RuleBuilder.() -> Unit,
    ) {
        val selector = baseSelector + select(styleClass)
        inheritStyleClass?.let { selector.inheritStyle(it) }
        RuleBuilder(stylesheet, selector).apply(block)
    }
}