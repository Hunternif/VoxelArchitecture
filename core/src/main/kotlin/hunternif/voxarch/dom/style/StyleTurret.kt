package hunternif.voxarch.dom.style

import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.sandbox.castle.turret.Turret
import hunternif.voxarch.util.RandomOption
import hunternif.voxarch.util.nextWeighted
import kotlin.math.roundToInt
import kotlin.random.Random

// A turret is created via TurretGenerator, by adding child nodes to a Room.
// Therefore, any styles must be applied to that generator.
// Potentially there can be multiple generators attached to this node, so
// we must apply the styles to all of them.

private inline fun StyledNode<*>.forTurretGenerators(
    crossinline block: TurretGenerator.() -> Unit
) {
    for (gen in domBuilder.generators) {
        if (gen is TurretGenerator) gen.block()
    }
}

private inline val StyledNode<*>.firstGenerator: TurretGenerator?
    get() = domBuilder.generators.filterIsInstance<TurretGenerator>().firstOrNull()

/** Offset for borders and spires in all child turrets. */
fun StyledNode<PolygonRoom>.roofOffset(block: StyleSize.() -> Dimension) {
    val baseValue = node.width
    val style = StyleSize()
    forTurretGenerators {
        roofOffset = style.block()
            .clamp(style.min, style.max)
            .invoke(baseValue, seed + 10000006)
            .roundToInt()
    }
}

/** Y/X ratio of spires for all child turrets. */
var StyledNode<PolygonRoom>.spireRatio: Double
    get() = firstGenerator?.spireRatio ?: 0.0
    set(value) { forTurretGenerators { spireRatio = value } }

/** Y/X ratio of tapered bottoms of turrets. */
var StyledNode<PolygonRoom>.taperRatio: Double
    get() = firstGenerator?.taperRatio ?: 0.0
    set(value) { forTurretGenerators { taperRatio = value } }

class StyleTurretShape : StyleParameter

fun StyledNode<PolygonRoom>.roofShape(block: StyleTurretShape.() -> Option<RoofShape>) {
    val base = when (val parent = node.parent) {
        is Turret -> parent.roofShape
        else -> RoofShape.FLAT_BORDERED
    }
    roofShape = StyleTurretShape().block().invoke(base, seed + 10000007)
}

var StyledNode<PolygonRoom>.roofShape: RoofShape
    get() = firstGenerator?.roofShape ?: RoofShape.FLAT_BORDERED
    set(value) { forTurretGenerators { roofShape = value } }

fun StyledNode<PolygonRoom>.bodyShape(block: StyleTurretShape.() -> Option<BodyShape>) {
    val base = when (val parent = node.parent) {
        is Turret -> parent.bodyShape
        else -> BodyShape.SQUARE
    }
    bodyShape = StyleTurretShape().block().invoke(base, seed + 10000008)
}

var StyledNode<PolygonRoom>.bodyShape: BodyShape
    get() = firstGenerator?.bodyShape ?: BodyShape.SQUARE
    set(value) {
        forTurretGenerators {
            bodyShape = value
            shape = bodyShape.toPolygonShape()
        }
    }

private fun BodyShape.toPolygonShape(): PolygonShape = when(this) {
    BodyShape.SQUARE -> PolygonShape.SQUARE
    BodyShape.ROUND -> PolygonShape.ROUND
}

fun StyledNode<PolygonRoom>.bottomShape(block: StyleTurretShape.() -> Option<BottomShape>) {
    val base = when (val parent = node.parent) {
        is Turret -> parent.bottomShape
        else -> BottomShape.FLAT
    }
    bottomShape = StyleTurretShape().block().invoke(base, seed + 10000009)
}

var StyledNode<PolygonRoom>.bottomShape: BottomShape
    get() = firstGenerator?.bottomShape ?: BottomShape.FLAT
    set(value) { forTurretGenerators { bottomShape = value } }

fun StyleTurretShape.randomRoof(): Option<RoofShape> = { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, RoofShape.FLAT_BORDERED),
        RandomOption(0.5, RoofShape.SPIRE),
        RandomOption(0.5, RoofShape.SPIRE_BORDERED)
    ).value
}

fun StyleTurretShape.randomBody(): Option<BodyShape> = { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, BodyShape.SQUARE),
        RandomOption(1.0, BodyShape.ROUND)
    ).value
}