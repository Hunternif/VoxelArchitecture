package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.findParentNode
import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.util.RandomOption
import hunternif.voxarch.util.nextWeighted
import kotlin.math.roundToInt
import kotlin.random.Random

// A turret is created via TurretGenerator, by adding child nodes to a Room.
// Therefore, any styles must be applied to that generator.
// Potentially there can be multiple generators attached to this node, so
// we must apply the styles to all of them.

/** Offset for borders and spires in all child turrets. */
fun StyledGen<TurretGenerator>.roofOffset(block: StyleSize.() -> Dimension) {
    val baseValue = domBuilder.findParentNode().width
    val style = StyleSize()
    gen.roofOffset = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000006)
        .roundToInt()
}

/** Y/X ratio of spires for all child turrets. */
var StyledGen<TurretGenerator>.spireRatio: Double
    get() = gen.spireRatio
    set(value) { gen.spireRatio = value }

/** Y/X ratio of tapered bottoms of turrets. */
var StyledGen<TurretGenerator>.taperRatio: Double
    get() = gen.taperRatio
    set(value) { gen.taperRatio = value }

class StyleTurretBodyShape : StyleParameter
class StyleTurretRoofShape : StyleParameter
class StyleTurretBottomShape : StyleParameter

fun StyledGen<TurretGenerator>.roofShape(block: StyleTurretRoofShape.() -> Value<RoofShape>) {
    val base =gen.roofShape
    gen.roofShape = StyleTurretRoofShape().block().invoke(base, seed + 10000007)
}

var StyledGen<TurretGenerator>.roofShape: RoofShape
    get() = gen.roofShape
    set(value) { gen.roofShape = value }

// TODO: this will apply to a turret-with-room generator
//var StyledGen<TurretGenerator>.bodyShape: BodyShape
//    get() = gen.bodyShape
//    set(value) { gen.bodyShape = value }

//fun StyledGen<TurretGenerator>.bodyShape(block: StyleTurretBodyShape.() -> Option<BodyShape>) {
//    val base = gen.bodyShape
//    gen.bodyShape = StyleTurretBodyShape().block().invoke(base, seed + 10000008)
//}

fun StyledGen<TurretGenerator>.bottomShape(block: StyleTurretBottomShape.() -> Value<BottomShape>) {
    val base = gen.bottomShape
    gen.bottomShape = StyleTurretBottomShape().block().invoke(base, seed + 10000009)
}

var StyledGen<TurretGenerator>.bottomShape: BottomShape
    get() = gen.bottomShape
    set(value) { gen.bottomShape = value }

fun StyleTurretRoofShape.randomRoof(): Value<RoofShape> = value { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, RoofShape.FLAT_BORDERED),
        RandomOption(0.5, RoofShape.SPIRE),
        RandomOption(0.5, RoofShape.SPIRE_BORDERED)
    ).value
}

fun StyleTurretBodyShape.randomBody(): Value<BodyShape> = value { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, BodyShape.SQUARE),
        RandomOption(1.0, BodyShape.ROUND)
    ).value
}