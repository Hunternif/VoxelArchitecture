package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.DomDsl
import hunternif.voxarch.dom.style.StyledElement

/**
 * Base class for DOM elements.
 * DOM Builder should not have any state dependent on which parent it's
 * attached to. This will allow to execute DOM builder under any root.
 */
@DomDsl
open class DomBuilder {

    /** Offset from the main seed for randomized properties.
     * Can be modified per DOM builder. */
    var seedOffset: Long = 0

    /** Don't manually add children, use [addChild] instead.*/
    val children = linkedSetOf<DomBuilder>()

    /** Whether the node and its children will be built or ignored. */
    var visibility: Visibility = Visibility.VISIBLE

    /** List of "CSS classes" applied to this element. */
    val styleClass = linkedSetOf<String>()

    /** Extension slots where other DomBuilders attach. */
    val slots = mutableMapOf<String, DomBuilder>()

    /** If hinting is enabled, children's global positions will be rounded to int,
     * according to this strategy. */
    var hintDir: HintDir = HintDir.ROUND

    /** The unique class name ensures that the following style rules
     * will only apply to this turret instance. */
    internal val uniqueClass by lazy {
        "$TECH_STYLE_PREFIX${this::class.java.simpleName}_${hashCode()}"
    }

    init {
        addStyle(uniqueClass)
    }

    /**
     * Layout pass 0: select children that will be rendered.
     * - Can override to return different instances.
     * - Can add styles to stylesheet, but only once.
     *   (This is called before children are styled)
     */
    open fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> =
        children

    /**
     * Layout pass 1: create an element for styling and measuring.
     * - Can create nodes and add them to parent, without setting size.
     * - Must not add new child DOM builders.
     * - Must be called only once per DOM tree, because it can modify parent node.
     */
    open fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> =
        StyledElement(this, ctx)

    /**
     * Layout pass 2: modify child position & size as necessary.
     * - Can override to return different instances.
     */
    open fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> =
        children

    /**
     * Layout pass 3: any final adjustments after this element has been styled
     * and laid out inside the parent.
     * Its children have not been prepared yet.
     */
    open fun postLayout(element: StyledElement<*>) {}

    fun addChild(child: DomBuilder) {
        children.add(child)
    }

    fun removeChild(child: DomBuilder) {
        children.remove(child)
    }

    /** Add given style class name to this builder. */
    fun addStyle(style: String): DomBuilder {
        styleClass.add(style)
        return this
    }

    fun addStyles(vararg styles: String): DomBuilder {
        styleClass.addAll(styles)
        return this
    }

    fun addAllStyles(styles: Iterable<String>): DomBuilder {
        styleClass.addAll(styles)
        return this
    }

    /** Add given style class names to this builder. */
    operator fun Array<out String>.unaryPlus() {
        styleClass.addAll(this)
    }

    fun addSlot(name: String, domSlot: DomBuilder) {
        slots[name] = domSlot
    }

    companion object {
        /** Style classes with this prefix will not be copied to node tags */
        const val TECH_STYLE_PREFIX = "_x_"
    }
}

enum class Visibility {
    VISIBLE,

    /** The node's children will not be created. */
    GONE
}
