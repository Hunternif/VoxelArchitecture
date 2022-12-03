package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.style.StyleParameter
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

typealias StyleRuleInfoAny = StyleRuleInfo<*, *, *>

abstract class StyleRuleInfo<N : Node, P : StyleParameter, V : Any>(
    val name: String,
    val nodeClass: KClass<N>,
    val parameterClass: KClass<P>,
    val valueClass: KClass<V>,
) {
    abstract fun StyledNode<N>.rule(paramBlock: P.() -> V)
    fun addStyleForNodeClass(stylesheet: Stylesheet, paramBlock: StyleParameter.() -> V) {
        // TODO: migrate to style2
//        stylesheet.styleFor(nodeClass) {
//            rule(paramBlock)
//        }
    }
}

val allRules: List<StyleRuleInfoAny> by lazy {
    listOf(
//        rule(StyledNode<Node>::align) // TODO: migrate to style2,
//        rule(StyledNode<Node>::x), // TODO: migrate to style2
//        rule(StyledNode<Node>::y), // TODO: migrate to style2
//        rule(StyledNode<Node>::z), // TODO: migrate to style2
//        rule(StyledNode<Node>::position), // TODO: support vectors
//        rule(StyledNode<Node>::height),  // TODO: migrate to style2
//        rule(StyledNode<Node>::width),  // TODO: migrate to style2
//        rule(StyledNode<Node>::length),  // TODO: migrate to style2
//        rule(StyledNode<Node>::diameter), // TODO: migrate to style2
//        rule(StyledNode<Node>::size), // TODO: support vectors
//        rule(StyledNode<Node>::visibility), // only useful with logic, too complex for UI
//        rule(StyledNode<PolygonRoom>::shape), // TODO: migrate to style2
//        rule(StyledNode<PolygonRoom>::edgeLength), // TODO: migrate to style2
//        rule(StyledNode<Room>::roofOffset), // TODO: support generator styles
//        rule(StyledNode<Room>::spireRatio), // TODO: make into DSL builder
//        rule(StyledNode<Room>::taperRatio), // TODO: make into DSL builder
//        rule(StyledNode<Room>::roofShape), // TODO: support generator styles
//        rule(StyledNode<Room>::bodyShape), // TODO: support generator styles
//        rule(StyledNode<Room>::bottomShape), // TODO: support generator styles
    )
}

private inline fun <reified N : Node, reified P : StyleParameter, reified V : Any> rule(
    noinline method: StyledNode<N>.(P.() -> V) -> Unit,
): StyleRuleInfo<N, P, *> {
    val name = (method as KFunction<Unit>).name
    return object : StyleRuleInfo<N, P, V>(name, N::class, P::class, V::class) {
        override fun StyledNode<N>.rule(paramBlock: P.() -> V) {
            method(paramBlock)
        }

    }
}